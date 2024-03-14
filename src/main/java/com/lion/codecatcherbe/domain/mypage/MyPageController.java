package com.lion.codecatcherbe.domain.mypage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
