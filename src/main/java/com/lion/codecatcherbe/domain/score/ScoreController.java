package com.lion.codecatcherbe.domain.score;

import com.lion.codecatcherbe.domain.score.dto.request.ScoreProblemReq;
import com.lion.codecatcherbe.domain.score.dto.response.ScoreApiRes;
import com.lion.codecatcherbe.domain.score.dto.response.ScoreSubmitResultRes;
import com.lion.codecatcherbe.domain.score.dto.response.ScoreTestCaseResultRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/score")
public class ScoreController {

    private final ScoreService scoreService;

    @PostMapping("/mock/testcase")
    public ResponseEntity<ScoreTestCaseResultRes> ScoreMockTestCases(@RequestBody ScoreProblemReq scoreProblemReq) {
        ScoreApiRes r1 = new ScoreApiRes(false, null, "2 5", "10", "10", true);
        ScoreApiRes r2 = new ScoreApiRes(true, "line 1\n    gcd(a, b):\nIndentationError: unexpected indent\n", "3 9", "12", "null", false);

        ScoreTestCaseResultRes scoreTestCaseResultRes = ScoreTestCaseResultRes.builder()
            .testCase_1(r1)
            .testCase_2(r2)
            .build();

        return new ResponseEntity<>(scoreTestCaseResultRes, HttpStatus.OK);
    }

    @PostMapping("/mock/submit")
    public ResponseEntity<ScoreSubmitResultRes> SubmitCode(@RequestBody ScoreProblemReq scoreProblemReq) {
        ScoreApiRes r1 = new ScoreApiRes(false, null, "2 5", "10", "10", true);
        ScoreApiRes r2 = new ScoreApiRes(false, null, "6 10", "16", "16", true);
        ScoreApiRes r3 = new ScoreApiRes(false, null, "7 7", "14", "13", false);

        ScoreSubmitResultRes scoreSubmitResultRes = ScoreSubmitResultRes.builder()
            .isCorrect(false)
            .testCase_1(r1)
            .testCase_2(r2)
            .testCase_3(r3)
            .build();

        return new ResponseEntity<>(scoreSubmitResultRes, HttpStatus.OK);
    }

    @PostMapping("/testcase")
    public ResponseEntity<ScoreTestCaseResultRes> ScoreTestCases(@RequestBody ScoreProblemReq scoreProblemReq) {
        return scoreService.getScoreTestCasesResult(scoreProblemReq);
    }

    @PostMapping("/submit/retry")
    public ResponseEntity<ScoreSubmitResultRes> SubmitRetryCode(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody ScoreProblemReq scoreProblemReq) {
        return scoreService.getScoreSubmitRetryResult(token, scoreProblemReq);
    }
}
