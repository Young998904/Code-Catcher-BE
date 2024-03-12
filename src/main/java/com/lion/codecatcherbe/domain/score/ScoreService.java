package com.lion.codecatcherbe.domain.score;

import com.lion.codecatcherbe.domain.score.dto.request.ScoreApiReq;
import com.lion.codecatcherbe.domain.score.dto.response.ScoreApiRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ScoreService {
    @Value("${score.url}")
    private String REDIRECT_HOST;
    public ScoreApiRes getResultFromApi (String type, String code, String input, String output) {
        RestTemplate rt = new RestTemplate();
        String url = REDIRECT_HOST + "/" + type;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ScoreApiReq> request = new HttpEntity<>(new ScoreApiReq(code, input, output), headers);

        return rt.postForObject(url, request, ScoreApiRes.class);
    }
}
