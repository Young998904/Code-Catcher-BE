package com.lion.codecatcherbe.domain.bookmark;

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
}