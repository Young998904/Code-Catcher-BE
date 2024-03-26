package com.lion.codecatcherbe.domain.mypage;

import com.lion.codecatcherbe.domain.mypage.dto.BookmarkMoreInfoRes;
import com.lion.codecatcherbe.domain.mypage.dto.BookmarkMoreInfoRes.BookmarkMoreInfo;
import com.lion.codecatcherbe.domain.mypage.dto.MyPageInfoRes;
import com.lion.codecatcherbe.domain.mypage.dto.MyPageInfoRes.Achievement;
import com.lion.codecatcherbe.domain.mypage.dto.MyPageInfoRes.BookmarkInfo;
import com.lion.codecatcherbe.domain.mypage.dto.MyPageInfoRes.ProblemInfo;
import com.lion.codecatcherbe.domain.mypage.dto.ProblemMoreInfoRes;
import com.lion.codecatcherbe.domain.mypage.dto.ProblemMoreInfoRes.ProblemMoreInfo;
import com.lion.codecatcherbe.domain.user.model.User;
import com.lion.codecatcherbe.domain.user.repository.UserRepository;
import com.lion.codecatcherbe.infra.kakao.security.TokenProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.data.mongodb.core.query.Query;
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
        List<BookmarkInfo> bookmarkDetails = findTop4RecentBookmarkDetails(userId);

        // 지난 테스트 문제 최근 4개 리스트 가져오기
        List<ProblemInfo> problemDetails = findTop4RecentProblemDetails(user.getCreatedAt().truncatedTo(ChronoUnit.DAYS), userId);

        // 해당 년,월 월간 달성률 리스트 가져오기
        List<Achievement> achievementList = findAchievementList(userId, LocalDateTime.now().plusHours(9L).getYear(), LocalDateTime.now().plusHours(9L).getMonthValue());

        MyPageInfoRes myPageInfoRes = MyPageInfoRes.builder()
            .bookmarkInfo(bookmarkDetails)
            .problemInfo(problemDetails)
            .achieveInfo(achievementList)
            .build();

        return new ResponseEntity<>(myPageInfoRes, HttpStatus.OK);
    }

    private List<Achievement> findAchievementList(String userId, int year, int month) {
        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = LocalDate.of(year, month, 1).plusMonths(1).atStartOfDay().minusSeconds(1L);

        MatchOperation matchOperation = Aggregation.match(Criteria
            .where("createdAt").gte(start).lt(end)
            .and("userId").is(userId));
        SortOperation sortOperation = Aggregation.sort(Sort.by(Direction.ASC, "createdAt"));
        ProjectionOperation projectionOperation = Aggregation.project("cnt", "createdAt");
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, sortOperation, projectionOperation);

        AggregationResults<Achievement> results = mongoOperations.aggregate(
            aggregation, "achieve", Achievement.class);

        return results.getMappedResults();
    }

    private List<ProblemInfo> findTop4RecentProblemDetails(LocalDateTime signedAt, String userId) {
        // 유저의 createdAt 을 고려해서 하루 전 기준으로 4개를 가지고 와야함
        LocalDateTime start = LocalDateTime.now().plusHours(9L).truncatedTo(ChronoUnit.DAYS).minusDays(30);
        LocalDateTime end = LocalDateTime.now().plusHours(9L).truncatedTo(ChronoUnit.DAYS).minusSeconds(1);

        // 가입일이 한달 전일 경우 조회 범위 조정
        if (signedAt.isAfter(start)) start = signedAt;

        MatchOperation matchOperation = Aggregation.match(Criteria.where("createdAt").gte(start).lte(end));
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt"));
        ProjectionOperation projectionOperation = Aggregation.project("level", "title", "createdAt").and("_id").as("problemId");
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, sortOperation, projectionOperation, Aggregation.limit(4));

        AggregationResults<ProblemInfo> results = mongoOperations.aggregate(
            aggregation, "problem", ProblemInfo.class);

        return results.getMappedResults();
    }
    public List<BookmarkInfo> findTop4RecentBookmarkDetails (String userId) {
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
            .and("createdAt").as("createdAt")
            .and("_id").as("bookmarkId");
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
        AggregationResults<BookmarkInfo> results = mongoOperations.aggregate(
            aggregation, "bookmark", BookmarkInfo.class);

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

    public ResponseEntity<BookmarkMoreInfoRes> getBookmarkList(String token, int page) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 전체 문서 개수 확인
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        long total = mongoOperations.count(query, "bookmark");

        // 페이징 및 결과 반환
        Pageable pageable = PageRequest.of(page, 10);

        List<BookmarkMoreInfo> bookmarkMoreInfoResList = findMoreBookmarkDetails(userId, pageable);

        // 리턴 객체 구성
        BookmarkMoreInfoRes bookmarkMoreInfoRes = BookmarkMoreInfoRes.builder()
            .questionData(bookmarkMoreInfoResList)
            .currentPage(page)
            .totalPage((int) Math.ceil((double) total / 10) -1)
            .build();

        return new ResponseEntity<>(bookmarkMoreInfoRes, HttpStatus.OK);
    }

    public List<BookmarkMoreInfo> findMoreBookmarkDetails(String userId, Pageable pageable) {
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
            .and("problemInfo.subject").as("subject")
            .and("createdAt").as("createdAt")
            .and("_id").as("bookmarkId");
        // 정렬 조건 설정
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt"));
        // 페이지 처리
        long skip = pageable.getOffset();
        long limit = pageable.getPageSize();
        // Aggregation 생성 및 limit 설정
        Aggregation aggregation = Aggregation.newAggregation(
            matchOperation,
            lookupOperation,
            unwindOperation,
            projectionOperation,
            sortOperation,
            Aggregation.skip(skip),
            Aggregation.limit(limit)
        );
        // 실행
        AggregationResults<BookmarkMoreInfo> results = mongoOperations.aggregate(
            aggregation, "bookmark", BookmarkMoreInfo.class);

        return results.getMappedResults();
    }

    public ResponseEntity<ProblemMoreInfoRes> getProblemList(String token, int page) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(page, 10);
        List<ProblemMoreInfo> problemMoreInfoResList = findMoreProblemDetails(userId, pageable);

        Query query = new Query();
        long total = mongoOperations.count(query,"problem");

        ProblemMoreInfoRes problemMoreInfoRes = ProblemMoreInfoRes.builder()
            .questionData(problemMoreInfoResList)
            .currentPage(page)
            .totalPage((int) Math.ceil((double) total / 10) -1)
            .build();

        return new ResponseEntity<>(problemMoreInfoRes, HttpStatus.OK);
    }

    private List<ProblemMoreInfo> findMoreProblemDetails(String userId, Pageable pageable) {
        LocalDateTime end = LocalDateTime.now().plusHours(9L).truncatedTo(ChronoUnit.DAYS).minusSeconds(1);
        MatchOperation matchOperation = Aggregation.match(Criteria.where("createdAt").lte(end));
        LookupOperationWithPipeline lookupOperation = new LookupOperationWithPipeline("submit", "_id", "submits", userId);
        UnwindOperation unwindOperation = Aggregation.unwind("submits", true);
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt"));
        ProjectionOperation projectionOperation = Aggregation.project()
            .and("level").as("level")
            .and("_id").as("problemId")
            .and("title").as("title")
            .and("subject").as("subject")
            .and("createdAt").as("createdAt")
            .and("submits.isSuccess").as("status");
        long skip = pageable.getOffset();
        long limit = pageable.getPageSize();
        Aggregation aggregation = Aggregation.newAggregation(
            matchOperation,
            lookupOperation,
            unwindOperation,
            projectionOperation,
            sortOperation,
            Aggregation.skip(skip),
            Aggregation.limit(limit)
        );
        AggregationResults<ProblemMoreInfo> results = mongoOperations.aggregate(
            aggregation, "problem", ProblemMoreInfo.class);

        return results.getMappedResults();
    }
}
