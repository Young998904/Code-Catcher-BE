package com.lion.codecatcherbe.infra.social.google;

import com.google.gson.Gson;
import com.lion.codecatcherbe.domain.user.UserService;
import com.lion.codecatcherbe.domain.user.model.User;
import com.lion.codecatcherbe.domain.user.repository.UserRepository;
import com.lion.codecatcherbe.infra.social.dto.GoogleInfoRes;
import com.lion.codecatcherbe.infra.social.dto.GoogleOAuthRes;
import com.lion.codecatcherbe.infra.social.dto.SuccessLoginInfo;
import com.lion.codecatcherbe.infra.social.security.TokenProvider;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    @Value("${oauth.google.client-id}")
    private String GOOGLE_CLIENT_ID;
    @Value("${oauth.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    public SuccessLoginInfo googleLogin(String accessCode, String host) {
        // 유저 정보 확인
        GoogleInfoRes googleUserInfo = getGoogleUserInfo(accessCode, host);

        // 유저 가입 여부 확인 및 가입
        Map<String, Object> userInfo = registerGoogleUserIfNeed(googleUserInfo);
        User googleUser = (User) userInfo.get("user");
        boolean isNew = (boolean) userInfo.get("isNew");

        // 로그인 후 jwt 리턴
        String jwt = login(googleUser);

        return SuccessLoginInfo.builder()
            .jwt(jwt)
            .isNew(isNew)
            .nickname(googleUser.getName())
            .userId(googleUser.getKakaoId())
            .email(googleUser.getEmail())
            .level(googleUser.getLevel())
            .exp(googleUser.getExp())
            .expUpper(userService.getExpUpper(googleUser.getLevel()))
            .totalCnt(userService.getCnt(googleUser)[0])
            .completeCnt(userService.getCnt(googleUser)[1])
            .bookmarkCnt(userService.getCnt(googleUser)[2])
            .build();
    }
    private GoogleInfoRes getGoogleUserInfo(String accessCode, String host) {
        RestTemplate restTemplate=new RestTemplate();
        Map<String, String> params = new HashMap<>();

        params.put("code", accessCode);
        params.put("client_id", GOOGLE_CLIENT_ID);
        params.put("client_secret", GOOGLE_CLIENT_SECRET) ;
        params.put ("redirect_uri", host + "/google/callback");
        params.put ("grant_type", "authorization_code");

        ResponseEntity<GoogleOAuthRes> responseEntity=restTemplate.postForEntity(GOOGLE_TOKEN_URL, params, GoogleOAuthRes.class);

        String decodedInfo = decryptBase64UrlToken(responseEntity.getBody().getId_token().split("\\.")[1]);

        Gson gson = new Gson();

        GoogleInfoRes googleInfoRes = gson.fromJson(decodedInfo, GoogleInfoRes.class);

        return googleInfoRes;
    }
    private Map<String, Object> registerGoogleUserIfNeed (GoogleInfoRes googleUserInfo) {
        // 기존 유저 존재 여부 확인
        String email = googleUserInfo.getEmail();
        String[] split = email.split("@");
        String nickname = split[0];
        Long id = (long) googleUserInfo.getIat();

        User user =  userRepository.findByEmail(googleUserInfo.getEmail()).orElse(null);

        boolean isNew = false;

        if (user == null) { // 회원가입
            user = User.builder()
                .kakaoId(id)
                .name(nickname)
                .email(email)
                .build();

            userRepository.save(user);
            isNew = true;
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", user);
        userInfo.put("isNew", isNew);

        return userInfo;
    }

    private String login(User user) {
        return tokenProvider.createToken(user);
    }

    public String decryptBase64UrlToken(String jwtToken) {
        Base64.Decoder decoder = Base64.getUrlDecoder();

        byte[] decodeBytes = decoder.decode(jwtToken);

        return new String(decodeBytes, StandardCharsets.UTF_8);
    }
}
