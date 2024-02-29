package com.lion.codecatcherbe.infra.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.codecatcherbe.domain.user.UserRepository;
import com.lion.codecatcherbe.domain.user.model.User;
import com.lion.codecatcherbe.infra.kakao.dto.SocialUserInfoDto;
import com.lion.codecatcherbe.infra.kakao.security.TokenProvider;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoService {
    @Value("${oauth.kakao.client-id}")
    private String CLIENT_ID;
    @Value("${oauth.kakao.url.host}")
    private String REDIRECT_HOST;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    public String kakaoLogin(String code)
        throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

//        System.out.printf("엑세스 코드 호출 성공 \n" + accessToken + "\n");

        // 2. 토큰으로 카카오 API 호출
        SocialUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
//        String userInfo =
//            "카카오 id :" + kakaoUserInfo.getId().toString() + "\n" +
//                "유저 nickname : " + kakaoUserInfo.getNickname() + "\n" +
//                "유저 email : " + kakaoUserInfo.getEmail();
//
//        System.out.printf("유저 정보 가져오기 성공 \n" + userInfo);

        // 3. 카카오ID로 회원가입 처리
        User kakaoUser = registerKakaoUserIfNeed(kakaoUserInfo);

//        System.out.println("회원 가입 성공");

//        // 4. 강제 로그인 처리
//        Authentication authentication = forceLogin(kakaoUser);
//
//        // 5. response Header에 JWT 토큰 추가
//        kakaoUsersAuthorizationInput(authentication, response);
//        return kakaoUserInfo;

        // 로그인 후 jwt 리턴
//        System.out.println("jwt 생성 : " + jwt);
        return login(kakaoUser);
    }

    private String getAccessToken(String code)
        throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", CLIENT_ID);
        body.add("redirect_uri", REDIRECT_HOST + "/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
            "https://kauth.kakao.com/oauth/token",
            HttpMethod.POST,
            kakaoTokenRequest,
            String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }
    private SocialUserInfoDto getKakaoUserInfo(String accessToken)
        throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.POST,
            kakaoUserInfoRequest,
            String.class
        );

        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties")
            .get("nickname").asText();

        return new SocialUserInfoDto(id, nickname, email);

    }
    private User registerKakaoUserIfNeed(SocialUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 email이 있는지 확인
        String kakaoEmail = kakaoUserInfo.getEmail();
        String nickname = kakaoUserInfo.getNickname();
        Long kakaoId = kakaoUserInfo.getId();

        User user = userRepository.findByEmail(kakaoEmail).orElse(null);

        if (user == null) {
            // 회원가입
            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password); // 아직은 필요하지 않음

            user = User.builder()
                .kakaoId(kakaoId)
                .name(nickname)
                .email(kakaoEmail)
                .build();

            userRepository.save(user);
        }

        return user;
    }
    private String login(User user) {
        return tokenProvider.createToken(user);
    }
}
