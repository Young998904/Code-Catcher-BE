package com.lion.codecatcherbe.infra.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lion.codecatcherbe.infra.kakao.dto.SuccessLoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kakao")
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoService kakaoService;

    @RequestMapping("/callback")
    public ResponseEntity<SuccessLoginInfo> kakaoLogin (@RequestParam String code) throws JsonProcessingException {
//        System.out.printf("인가 코드 호출 성공 \n" + code + "\n");
        SuccessLoginInfo info = kakaoService.kakaoLogin(code);
        return new ResponseEntity<>(info, generateHeader(info.getJwt()), HttpStatus.OK);
    }

    private HttpHeaders generateHeader(String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-access-token", jwt);
        return headers;
    }
}