package com.lion.codecatcherbe.domain.score.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScoreSubmitResultRes {
    private boolean isCorrect;
    private ScoreTestCaseResultRes.Result testCase_1;
    private ScoreTestCaseResultRes.Result testCase_2;
    private ScoreTestCaseResultRes.Result testCase_3;

    @Getter
    @Builder
    public static class Result {
        private boolean error;
        private String error_message;
        private String input;
        private String expected_output;
        private String actual_output;
        private boolean correct;
    }
}