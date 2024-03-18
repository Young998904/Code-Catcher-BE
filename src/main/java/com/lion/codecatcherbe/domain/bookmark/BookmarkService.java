package com.lion.codecatcherbe.domain.bookmark;

import com.lion.codecatcherbe.domain.bookmark.dto.BookMarkInfoRes;
import com.lion.codecatcherbe.domain.bookmark.dto.BookMarkReq;
import com.lion.codecatcherbe.domain.bookmark.model.Bookmark;
import com.lion.codecatcherbe.domain.coding.model.Problem;
import com.lion.codecatcherbe.domain.coding.repository.ProblemRepository;
import com.lion.codecatcherbe.domain.user.repository.UserRepository;
import com.lion.codecatcherbe.domain.user.model.User;
import com.lion.codecatcherbe.infra.kakao.security.TokenProvider;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ProblemRepository problemRepository;
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
    public HttpStatus saveBookmark(String token, BookMarkReq bookMarkReq) {
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return HttpStatus.UNAUTHORIZED;
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return HttpStatus.NOT_FOUND;
        }

        Problem problem = problemRepository.findById(bookMarkReq.getProblemId()).orElse(null);

        if (problem == null) {
            return HttpStatus.NOT_FOUND;
        }

        Bookmark bookmark = Bookmark.builder()
            .userId(userId)
            .problemId(bookMarkReq.getProblemId())
            .createdAt(LocalDateTime.now().plusHours(9L))
            .codeType(bookMarkReq.getCodeType())
            .code(bookMarkReq.getCode())
            .build();

        bookmarkRepository.save(bookmark);

        return HttpStatus.CREATED;
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

        Problem problem = problemRepository.findById(bookmark.getProblemId()). orElse(null);

        String codeType = bookmark.getCodeType();
        String gptCode;
        String gptExplain;

        if (codeType.equals("java")) {
            gptCode = problem.getJava_code();
            gptExplain = problem.getJava_explain();
        }
        else {
            gptCode = problem.getPython_code();
            gptExplain = problem.getPython_explain();
        }

        BookMarkInfoRes bookMarkInfoRes = BookMarkInfoRes.builder()
            .myCode(bookmark.getCode())
            .codeType(codeType)
            .title(problem.getTitle())
            .subject(problem.getSubject())
            .script(problem.getScript())
            .input_condition(problem.getInput_condition())
            .output_condition(problem.getOutput_condition())
            .input_1(problem.getInput_1())
            .output_1(problem.getOutput_1())
            .input_2(problem.getInput_2())
            .output_2(problem.getOutput_2())
            .gptCode(gptCode)
            .gptExplain(gptExplain)
            .build();

        return new ResponseEntity<>(bookMarkInfoRes, HttpStatus.OK);
    }
}
