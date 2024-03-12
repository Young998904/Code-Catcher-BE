package com.lion.codecatcherbe.domain.score.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScoreSubmitResultRes {
    private boolean isCorrect;
    private ScoreApiRes testCase_1;
    private ScoreApiRes testCase_2;
    private ScoreApiRes testCase_3;
}
