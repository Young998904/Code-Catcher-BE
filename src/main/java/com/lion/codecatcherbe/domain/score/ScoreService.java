package com.lion.codecatcherbe.domain.score;

import com.lion.codecatcherbe.domain.coding.model.Problem;
import com.lion.codecatcherbe.domain.coding.repository.ProblemRepository;
import com.lion.codecatcherbe.domain.score.dto.request.ScoreApiReq;
import com.lion.codecatcherbe.domain.score.dto.request.ScoreProblemReq;
import com.lion.codecatcherbe.domain.score.dto.response.ScoreApiRes;
import com.lion.codecatcherbe.domain.score.dto.response.ScoreTestCaseResultRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ScoreService {
    @Value("${score.url}")
    private String REDIRECT_HOST;

    private final ProblemRepository problemRepository;
    public ScoreApiRes getResultFromApi (String type, String code, String input, String output) {
        RestTemplate rt = new RestTemplate();
        String url = REDIRECT_HOST + "/" + type;

        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ScoreApiReq> request = new HttpEntity<>(new ScoreApiReq(code, input, output), headers);

        return rt.postForObject(url, request, ScoreApiRes.class);
    }

    public ResponseEntity<ScoreTestCaseResultRes> getScoreTestCasesResult(ScoreProblemReq scoreProblemReq) {
        Problem problem = problemRepository.findById(scoreProblemReq.getProblemId()).orElse(null);

        if (problem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String code = scoreProblemReq.getCode();
        String type = scoreProblemReq.getCodeType();

        ScoreTestCaseResultRes scoreTestCaseResultRes = ScoreTestCaseResultRes.builder()
            .testCase_1(getResultFromApi(type, code, problem.getInput_1(), problem.getOutput_1()))
            .testCase_2(getResultFromApi(type, code, problem.getInput_2(), problem.getOutput_2()))
            .build();

        return new ResponseEntity<>(scoreTestCaseResultRes, HttpStatus.OK);
    }
}
