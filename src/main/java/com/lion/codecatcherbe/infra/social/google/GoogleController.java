package com.lion.codecatcherbe.infra.social.google;

import com.lion.codecatcherbe.infra.social.dto.SuccessLoginInfo;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GoogleController {
    private final GoogleService googleService;

    @GetMapping("/callback")
    public  ResponseEntity<SuccessLoginInfo> successGoogleLogin(HttpServletRequest request,  @RequestParam String code) {
        String host = request.getHeader("X-Forwarded-Host");
        log.info("host : {}", host);
        log.info("code : {}", code);
        SuccessLoginInfo info = googleService.googleLogin(code, host);
        return new ResponseEntity<>(info, generateHeader(info.getJwt()), HttpStatus.OK);
    }

    private HttpHeaders generateHeader(String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-access-token", jwt);
        return headers;
    }
}
