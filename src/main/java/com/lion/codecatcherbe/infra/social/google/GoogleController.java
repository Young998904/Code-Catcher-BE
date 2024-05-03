package com.lion.codecatcherbe.infra.social.google;

import com.lion.codecatcherbe.infra.social.dto.SuccessLoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/google")
@RequiredArgsConstructor
public class GoogleController {
    private final GoogleService googleService;

    @GetMapping("/login/callback")
    public  ResponseEntity<SuccessLoginInfo> successGoogleLogin(@RequestParam("code") String accessCode) {
        System.out.println(accessCode);
        SuccessLoginInfo info = googleService.googleLogin(accessCode);
        return new ResponseEntity<>(info, generateHeader(info.getJwt()), HttpStatus.OK);
    }

    private HttpHeaders generateHeader(String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-access-token", jwt);
        return headers;
    }
}
