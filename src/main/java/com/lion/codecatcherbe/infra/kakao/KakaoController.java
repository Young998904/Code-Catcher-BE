package com.lion.codecatcherbe.infra.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    public ResponseEntity<JwtDto> kakaoLogin (@RequestParam String code) throws JsonProcessingException {
//        System.out.printf("인가 코드 호출 성공 \n" + code + "\n");
        String jwt = kakaoService.kakaoLogin(code);
        return new ResponseEntity<>(new JwtDto(jwt), generateHeader(jwt), HttpStatus.OK);
    }

    private HttpHeaders generateHeader(String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-access-token", jwt);
        return headers;
    }
}