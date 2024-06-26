package com.lion.codecatcherbe.domain.user;

import com.lion.codecatcherbe.domain.bookmark.BookmarkRepository;
import com.lion.codecatcherbe.domain.bookmark.model.Bookmark;
import com.lion.codecatcherbe.domain.score.model.Submit;
import com.lion.codecatcherbe.domain.score.repository.SubmitRepository;
import com.lion.codecatcherbe.domain.user.dto.NicNameDto;
import com.lion.codecatcherbe.domain.user.dto.response.UserInfoRes;
import com.lion.codecatcherbe.domain.user.model.Achieve;
import com.lion.codecatcherbe.domain.user.model.User;
import com.lion.codecatcherbe.domain.user.repository.AchieveRepository;
import com.lion.codecatcherbe.domain.user.repository.UserRepository;
import com.lion.codecatcherbe.infra.social.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AchieveRepository achieveRepository;
    private final BookmarkRepository bookmarkRepository;
    private final SubmitRepository submitRepository;
    private final MongoOperations mongoOperations;
    private final MongoTemplate mongoTemplate;

    private static final int[] LEVEL_UP_EXPERIENCE = {0, 90, 160, 250, 360};

    public ResponseEntity<NicNameDto> updateUserNickname(String token, NicNameDto nicNameDto) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        user.setUserName(nicNameDto.getNickname());

        userRepository.save(user);

        return new ResponseEntity<>(nicNameDto, HttpStatus.OK);
    }

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

    public HttpStatus deleteUser(String token) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return HttpStatus.UNAUTHORIZED;
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return HttpStatus.NOT_FOUND;
        }

        userRepository.delete(user);
        bookmarkRepository.deleteAllByUserId(userId);
        submitRepository.deleteAllByUserId(userId);
        achieveRepository.deleteAllByUserId(userId);

        return HttpStatus.OK;
    }

    public void addExp(User user) {
        // 현재 level 과 exp 가지고 옴
        int lev = user.getLevel();
        int exp = user.getExp();

        // 경험치 10 추가
        exp += 10;

        // level up 여부 확인
        if (exp >= LEVEL_UP_EXPERIENCE[lev]) {
            exp -= LEVEL_UP_EXPERIENCE[lev];
            lev += 1;
        }
        user.setLevAndExp(lev, exp);

        userRepository.save(user);
    }

    public void addAchieve(Achieve achieve) {
        int cnt = achieve.getCnt();
        achieve.setCnt(cnt + 1);

        achieveRepository.save(achieve);
    }

    public ResponseEntity<UserInfoRes> getUserInfo(String token) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        int[] cntArr = getCnt(user);

        UserInfoRes userInfoRes = UserInfoRes.builder()
            .userId(user.getKakaoId())
            .nickname(user.getName())
            .email(user.getEmail())
            .level(user.getLevel())
            .exp(user.getExp())
            .expUpper(LEVEL_UP_EXPERIENCE[user.getLevel()])
            .totalCnt(cntArr[0])
            .completeCnt(cntArr[1])
            .bookmarkCnt(cntArr[2])
            .build();

        return new ResponseEntity<>(userInfoRes, HttpStatus.OK);
    }

    public int getExpUpper(int level) {
        return LEVEL_UP_EXPERIENCE[level];
    }

    public int[] getCnt(User user) {
        int totalCnt, completeCnt, bookmarkCnt;
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(user.getId()));

        totalCnt = (int) mongoOperations.count(query, Submit.class);
        bookmarkCnt = (int) mongoOperations.count(query, Bookmark.class);

        query.addCriteria(Criteria.where("isSuccess").is(true));
        completeCnt = (int) mongoOperations.count(query, Submit.class);

        return new int[]{totalCnt, completeCnt, bookmarkCnt};
    }

    @Scheduled(cron = "0 00 15 * * ?")
    public void userIsUsedUpdate () {
        Update update = new Update().set("isUsed", false);
        mongoTemplate.updateMulti(new Query(), update, User.class);
    }

    public void isUsedToTrue(String token) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        User user = userRepository.findById(userId).orElse(null);

        user.toggleIsUsed();

        userRepository.save(user);
    }
}
