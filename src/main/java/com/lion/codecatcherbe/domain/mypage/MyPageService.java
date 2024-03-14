package com.lion.codecatcherbe.domain.mypage;

import com.lion.codecatcherbe.domain.mypage.MyPageInfoRes.Achievement;
import com.lion.codecatcherbe.domain.mypage.MyPageInfoRes.Info;
import com.lion.codecatcherbe.domain.user.model.User;
import com.lion.codecatcherbe.domain.user.repository.UserRepository;
import com.lion.codecatcherbe.infra.kakao.security.TokenProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MyPageService {

    private final UserRepository userRepository;
    private final MongoOperations mongoOperations;

    public String filterJwt (String token) {
        String jwt = null;

        if (token != null && token.startsWith("Bearer ")) {
            jwt = token.substring(7);
        }

        // 토큰 사용하여 필요한 로직 처리
        return jwt;
    }

    public String getUserId(String jwt) {
        String userId;
        try {
            userId = TokenProvider.getSubject(jwt);
        } catch (Exception e) {
            return null;
        }
        return userId;
    }

    public ResponseEntity<MyPageInfoRes> getMyPageInfo(String token) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 북마크 최근 4개 리스트 가져오기
        List<Info> bookmarkDetails = findTop4RecentBookmarkDetails(userId);

        // 지난 테스트 문제 최근 4개 리스트 가져오기
        List<Info> problemDetails = findTop4RecentProblemDetails(user.getCreatedAt().truncatedTo(ChronoUnit.DAYS), userId);

        // 해당 년,월 월간 달성률 리스트 가져오기
        List<Achievement> achievementList = findAchievementList(userId, LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue());

        MyPageInfoRes myPageInfoRes = MyPageInfoRes.builder()
            .bookmarkInfo(bookmarkDetails)
            .problemInfo(problemDetails)
            .achieveInfo(achievementList)
            .build();

        return new ResponseEntity<>(myPageInfoRes, HttpStatus.OK);
    }

    private List<Achievement> findAchievementList(String userId, int year, int month) {
        MatchOperation matchOperation = Aggregation.match(Criteria
            .where("createdAt").gte(LocalDate.of(year, month, 1).atStartOfDay()).
            lt(LocalDate.of(year, month, 1).plusMonths(1).atStartOfDay())
            .and("userId").is(userId));
        SortOperation sortOperation = Aggregation.sort(Sort.by(Direction.ASC, "createdAt"));
        ProjectionOperation projectionOperation = Aggregation.project("cnt", "createdAt");
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, sortOperation, projectionOperation);

        AggregationResults<Achievement> results = mongoOperations.aggregate(
            aggregation, "achieve", Achievement.class);

        return results.getMappedResults();
    }

    private List<Info> findTop4RecentProblemDetails(LocalDateTime signedAt, String userId) {
        // 유저의 createdAt 을 고려해서 하루 전 기준으로 4개를 가지고 와야함
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(30);
        LocalDateTime end = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusSeconds(1);

        // 가입일이 한달 전일 경우 조회 범위 조정
        if (signedAt.isAfter(start)) start = signedAt;

        MatchOperation matchOperation = Aggregation.match(Criteria.where("createdAt").gte(start).lte(end));
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt"));
        ProjectionOperation projectionOperation = Aggregation.project("level", "title", "createdAt").and("_id").as("problemId");
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, sortOperation, projectionOperation, Aggregation.limit(10));

        AggregationResults<Info> results = mongoOperations.aggregate(
            aggregation, "problem", Info.class);

        return results.getMappedResults();
    }
    public List<Info> findTop4RecentBookmarkDetails (String userId) {
        // 조회 조건 설정
        MatchOperation matchOperation = Aggregation.match(Criteria.where("userId").is(userId));
        // 조인 조건 설정
        LookupOperation lookupOperation = LookupOperation.newLookup()
            .from("problem") // 조인할 컬렉션 이름
            .localField("problemId") // bookmark 컬렉션에서 join 에 사용할 대상
            .foreignField("_id") // problem 컬렉션에서 join 에 사용할 대상
            .as("problemInfo"); // 조인된 문서를 저장할 필드 이름
        // List 형태로 가져와진 problemInfo 를 각각의 필드로 수정
        UnwindOperation unwindOperation = Aggregation.unwind("problemInfo", true);
        // 가져올 필드 조건 설정
        ProjectionOperation projectionOperation = Aggregation.project()
            .and("problemInfo.level").as("level")
            .and("problemInfo._id").as("problemId")
            .and("problemInfo.title").as("title")
            .and("createdAt").as("createdAt");
        // 정렬 조건 설정
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt"));
        // Aggregation 생성 및 limit 설정
        Aggregation aggregation = Aggregation.newAggregation(
            matchOperation,
            lookupOperation,
            unwindOperation, // 배열을 개별 문서로 분리
            projectionOperation,
            sortOperation,
            Aggregation.limit(4)
        );
        // 실행
        AggregationResults<Info> results = mongoOperations.aggregate(
            aggregation, "bookmark", Info.class);

        return results.getMappedResults();
    }

    public ResponseEntity<List<Achievement>> getAchieveInfo(String token, int year, int month) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(findAchievementList(userId, year, month), HttpStatus.OK);
    }
}
