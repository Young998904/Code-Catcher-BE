package com.lion.codecatcherbe.domain.bookmark;

import com.lion.codecatcherbe.common.CodeType;
import com.lion.codecatcherbe.domain.bookmark.dto.request.BookMarkUpdateReq;
import com.lion.codecatcherbe.domain.bookmark.dto.response.BookMarkDeleteRes;
import com.lion.codecatcherbe.domain.bookmark.dto.response.BookMarkInfoRes;
import com.lion.codecatcherbe.domain.bookmark.dto.request.BookMarkSaveReq;
import com.lion.codecatcherbe.domain.bookmark.dto.response.BookMarkRecordRes;
import com.lion.codecatcherbe.domain.bookmark.model.Bookmark;
import com.lion.codecatcherbe.domain.coding.model.Problem;
import com.lion.codecatcherbe.domain.coding.repository.ProblemRepository;
import com.lion.codecatcherbe.domain.mypage.MyPageService;
import com.lion.codecatcherbe.domain.mypage.dto.BookmarkMoreInfoRes;
import com.lion.codecatcherbe.domain.mypage.dto.BookmarkMoreInfoRes.BookmarkMoreInfo;
import com.lion.codecatcherbe.domain.user.repository.UserRepository;
import com.lion.codecatcherbe.domain.user.model.User;
import com.lion.codecatcherbe.infra.social.security.TokenProvider;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ProblemRepository problemRepository;
    private final MyPageService myPageService;
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
    public ResponseEntity<String> saveBookmark(String token, BookMarkSaveReq bookMarkSaveReq) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Problem problem = problemRepository.findById(bookMarkSaveReq.getProblemId()).orElse(null);

        if (problem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Bookmark bookmark = Bookmark.builder()
            .userId(userId)
            .problemId(bookMarkSaveReq.getProblemId())
            .createdAt(LocalDateTime.now().plusHours(9L))
            .codeType(bookMarkSaveReq.getCodeType())
            .code(bookMarkSaveReq.getCode())
            .gptReview(bookMarkSaveReq.getGptReview().orElse(null))
            .build();

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        return new ResponseEntity<>(savedBookmark.getId(), HttpStatus.OK);
    }

    public ResponseEntity<BookMarkInfoRes> findBookmarkInfo(String token, String bookmarkId) {
        // 유저 정보 확인
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 북마크 가져오기
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElse(null);

        if (bookmark == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 접근 권한 확인
        if (!bookmark.getUserId().equals(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Problem 객체로 부터 코드 타입 기반 AI 추천 코드 및 설명 가져오기
        Problem problem = problemRepository.findById(bookmark.getProblemId()). orElse(null);

        CodeType codeType = CodeType.valueOf(bookmark.getCodeType().toUpperCase());
        String[] codeAndExplain = codeType.extractCode(problem);

        // 반환 객체 생성
        BookMarkInfoRes bookMarkInfoRes = BookMarkInfoRes.builder()
            .level(problem.getLevel())
            .myCode(bookmark.getCode())
            .codeType(codeType.toString().toLowerCase())
            .title(problem.getTitle())
            .subject(problem.getSubject())
            .script(problem.getScript())
            .input_condition(problem.getInput_condition())
            .output_condition(problem.getOutput_condition())
            .input_1(problem.getInput_1())
            .output_1(problem.getOutput_1())
            .input_2(problem.getInput_2())
            .output_2(problem.getOutput_2())
            .gptCode(codeAndExplain[0])
            .gptExplain(codeAndExplain[1])
            .gptReviewRes(bookmark.getGptReview())
            .build();

        return new ResponseEntity<>(bookMarkInfoRes, HttpStatus.OK);
    }

    public HttpStatus deleteOneBookmark(String token, String bookmarkId) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return HttpStatus.UNAUTHORIZED;
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return HttpStatus.NOT_FOUND;
        }

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElse(null);

        if (bookmark == null) {
            return HttpStatus.NOT_FOUND;
        }

        bookmarkRepository.delete(bookmark);

        return HttpStatus.OK;
    }

    public ResponseEntity<BookmarkMoreInfoRes> deleteManyBookmark(String token, BookMarkDeleteRes bookMarkDeleteRes) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 북마크에서 삭제
        List<Bookmark> bookmarkList = bookmarkRepository.findByIdIn(bookMarkDeleteRes.getBookmarkList());
        bookmarkRepository.deleteAll(bookmarkList);

        // 해당 페이지 정보 다시 보내기

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        long total = mongoOperations.count(query, "bookmark");
        Pageable pageable = PageRequest.of(bookMarkDeleteRes.getCurrentPage(), 10);

        List<BookmarkMoreInfo> bookmarkMoreInfoResList = myPageService.findMoreBookmarkDetails(userId, pageable);

        // 리턴 객체 구성
        BookmarkMoreInfoRes bookmarkMoreInfoRes = BookmarkMoreInfoRes.builder()
            .questionData(bookmarkMoreInfoResList)
            .currentPage(bookMarkDeleteRes.getCurrentPage())
            .totalPage((int) Math.ceil((double) total / 10) -1)
            .build();

        return new ResponseEntity<>(bookmarkMoreInfoRes, HttpStatus.OK);
    }

    public ResponseEntity<BookMarkRecordRes> findBookmarkRecord(String token, Long problemId) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Bookmark bookmark = bookmarkRepository.findByUserIdAndProblemId(userId, problemId).orElse(null);

        if (bookmark == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        BookMarkRecordRes bookMarkRecordRes = BookMarkRecordRes.builder()
            .bookmarkId(bookmark.getId())
            .createdAt(bookmark.getCreatedAt())
            .build();

        return new ResponseEntity<>(bookMarkRecordRes, HttpStatus.OK);
    }

    public HttpStatus updateBookmark(String token, String bookmarkId, BookMarkUpdateReq bookMarkUpdateReq) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return HttpStatus.UNAUTHORIZED;
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return HttpStatus.NOT_FOUND;
        }

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId).orElse(null);

        if (bookmark == null) {
            return HttpStatus.NOT_FOUND;
        }

        bookmark.updateBook(LocalDateTime.now().plusHours(9L), bookMarkUpdateReq.getMyCode(), bookMarkUpdateReq.getCodeType(), bookMarkUpdateReq.getGptReview()
            .orElse(null));

        bookmarkRepository.save(bookmark);

        return HttpStatus.OK;
    }
}
