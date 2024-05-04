package com.lion.codecatcherbe.infra.social.google;

import com.lion.codecatcherbe.infra.social.dto.SuccessLoginInfo;
import javax.servlet.http.HttpServletRequest;
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

    @GetMapping("/callback")
    public  ResponseEntity<SuccessLoginInfo> successGoogleLogin(HttpServletRequest request, @RequestParam("code") String accessCode) {
        String host = request.getHeader("X-Forwarded-Host");
        SuccessLoginInfo info = googleService.googleLogin(accessCode, host);
        return new ResponseEntity<>(info, generateHeader(info.getJwt()), HttpStatus.OK);
    }

    private HttpHeaders generateHeader(String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-access-token", jwt);
        return headers;
    }
}
