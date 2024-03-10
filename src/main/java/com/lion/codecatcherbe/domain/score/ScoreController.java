package com.lion.codecatcherbe.domain.score;

import com.lion.codecatcherbe.domain.score.dto.request.ScoreProblemReq;
import com.lion.codecatcherbe.domain.score.dto.response.ScoreTestCaseResultRes;
import com.lion.codecatcherbe.domain.score.dto.response.ScoreTestCaseResultRes.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/score")
public class ScoreController {
    @PostMapping("/mock/testcase")
    public ResponseEntity<ScoreTestCaseResultRes> ScoreTestCases(@RequestBody ScoreProblemReq scoreProblemReq) {
        Result r1 = Result.builder()
            .error(false)
            .error_message(null)
            .input("2 5")
            .expected_output("10")
            .actual_output("10")
            .correct(true)
            .build();
        Result r2 = Result.builder()
            .error(true)
            .error_message("line 1\n    gcd(a, b):\nIndentationError: unexpected indent\n")
            .input("3 9")
            .expected_output("12")
            .actual_output(null)
            .correct(false)
            .build();
        ScoreTestCaseResultRes scoreTestCaseResultRes = ScoreTestCaseResultRes.builder()
            .testCase_1(r1)
            .testCase_2(r2)
            .build();

        return new ResponseEntity<>(scoreTestCaseResultRes, HttpStatus.OK);
    }
}
