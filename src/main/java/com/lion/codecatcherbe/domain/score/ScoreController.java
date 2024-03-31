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

    @PostMapping("/testcase")
    public ResponseEntity<ScoreTestCaseResultRes> ScoreTestCases(@RequestBody ScoreProblemReq scoreProblemReq) {
        return scoreService.getScoreTestCasesResult(scoreProblemReq);
    }

    @PostMapping("/submit/retry")
    public ResponseEntity<ScoreSubmitResultRes> SubmitRetryCode(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody ScoreProblemReq scoreProblemReq) {
        return scoreService.getScoreSubmitRetryResult(token, scoreProblemReq);
    }

    @PostMapping("/submit/today")
    public ResponseEntity<ScoreSubmitResultRes> submitTodayCode(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody ScoreProblemReq scoreProblemReq) {
        return scoreService.getScoreSubmitTodayResult(token, scoreProblemReq);
    }
}
