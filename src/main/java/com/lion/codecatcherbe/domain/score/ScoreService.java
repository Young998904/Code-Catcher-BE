package com.lion.codecatcherbe.domain.score;

import com.lion.codecatcherbe.domain.coding.model.Problem;
import com.lion.codecatcherbe.domain.coding.repository.ProblemRepository;
import com.lion.codecatcherbe.domain.score.dto.request.ScoreApiReq;
import com.lion.codecatcherbe.domain.score.dto.request.ScoreProblemReq;
import com.lion.codecatcherbe.domain.score.dto.response.ScoreApiRes;
import com.lion.codecatcherbe.domain.score.dto.response.ScoreSubmitResultRes;
import com.lion.codecatcherbe.domain.score.dto.response.ScoreTestCaseResultRes;
import com.lion.codecatcherbe.domain.score.model.Submit;
import com.lion.codecatcherbe.domain.score.repository.SubmitRepository;
import com.lion.codecatcherbe.domain.user.UserRepository;
import com.lion.codecatcherbe.domain.user.model.User;
import com.lion.codecatcherbe.infra.kakao.security.TokenProvider;
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

    public String filterJwt (String token) {
        String jwt = null;

        if (token != null && token.startsWith("Bearer ")) {
            jwt = token.substring(7);
        }

        // 토큰 사용하여 필요한 로직 처리
        return jwt;
    }

    public String getUserId(String jwt) {
        String userId;
        try {
            userId = TokenProvider.getSubject(jwt);
        } catch (Exception e) {
            return null;
        }
        return userId;
    }

    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final SubmitRepository submitRepository;

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

    public ResponseEntity<ScoreSubmitResultRes> getScoreSubmitRetryResult(String token, ScoreProblemReq scoreProblemReq) {
        // (1) 채점 결과를 받아옴
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ScoreSubmitResultRes scoreSubmitResultRes = getScoreSubmitResult(scoreProblemReq);

        // (2) 채점 결과를 바탕으로 Submit 객체 생성 or 수정
        Submit submit = submitRepository.findByUserIdAndProblemId(userId, scoreProblemReq.getProblemId()).orElse(null);

        if (submit == null) { // 최초 제출인 경우
            submit = Submit.builder()
                .userId(userId)
                .problemId(scoreProblemReq.getProblemId())
                .isSuccess(scoreSubmitResultRes.isCorrect())
                .build();
        }

        // 최초 정답인 경우
        if (!submit.isSuccess() && scoreSubmitResultRes.isCorrect()) submit.toggleToSuccess();

        // 코드 갱신
        if (scoreProblemReq.getCodeType().equals("java")) submit.setLastSubmitJavaCode(scoreProblemReq.getCode());
        else submit.setLastSubmitPythonCode(scoreProblemReq.getCode());

        submitRepository.save(submit);

        return new ResponseEntity<>(scoreSubmitResultRes, HttpStatus.OK);
    }

    // 전체 테스트 케이스 3개를 채점
    public ScoreSubmitResultRes getScoreSubmitResult(ScoreProblemReq scoreProblemReq) {
        Problem problem = problemRepository.findById(scoreProblemReq.getProblemId()).orElse(null);

        String code = scoreProblemReq.getCode();
        String type = scoreProblemReq.getCodeType();

        boolean isCorrect = false;

        ScoreApiRes t1 = getResultFromApi(type, code, problem.getInput_1(), problem.getOutput_1());
        ScoreApiRes t2 = getResultFromApi(type, code, problem.getInput_2(), problem.getOutput_2());
        ScoreApiRes t3 = getResultFromApi(type, code, problem.getInput_3(), problem.getOutput_3());

        if (t1.isCorrect() && t2.isCorrect() && t3.isCorrect()) isCorrect = true;

        return ScoreSubmitResultRes.builder()
            .isCorrect(isCorrect)
            .testCase_1(t1)
            .testCase_2(t2)
            .testCase_3(t3)
            .build();
    }
}
