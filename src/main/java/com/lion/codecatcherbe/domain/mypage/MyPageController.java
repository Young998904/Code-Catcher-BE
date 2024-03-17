package com.lion.codecatcherbe.domain.mypage;

import com.lion.codecatcherbe.domain.mypage.dto.BookmarkMoreInfoRes;
import com.lion.codecatcherbe.domain.mypage.dto.MyPageInfoRes;
import com.lion.codecatcherbe.domain.mypage.dto.MyPageInfoRes.Achievement;
import com.lion.codecatcherbe.domain.mypage.dto.ProblemMoreInfoRes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/my-page")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping()
    public ResponseEntity<MyPageInfoRes> getMyPageInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        return myPageService.getMyPageInfo(token);
    }

    @GetMapping("/achievement")
    public ResponseEntity<List<Achievement>> getAchieveInfo(@RequestHeader(value = "Authorization", required = false) String token, @RequestParam int year, @RequestParam int month) {
        return myPageService.getAchieveInfo(token, year, month);
    }

    @GetMapping("/bookmark")
    public ResponseEntity<BookmarkMoreInfoRes> getMoreBookmarkList (@RequestHeader(value = "Authorization", required = false) String token, @RequestParam (defaultValue = "0") int page) {
        return myPageService.getBookmarkList(token, page);
    }

    @GetMapping("question")
    public ResponseEntity<ProblemMoreInfoRes> getMoreQuestionList (@RequestHeader(value = "Authorization", required = false) String token, @RequestParam (defaultValue = "0") int page) {
        return myPageService.getProblemList(token, page);
    }
}
