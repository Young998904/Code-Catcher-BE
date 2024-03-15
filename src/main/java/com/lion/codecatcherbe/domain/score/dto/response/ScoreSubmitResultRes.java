package com.lion.codecatcherbe.domain.score.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class ScoreSubmitResultRes {
    private boolean isCorrect;
    @Setter
    @Builder.Default
    private boolean isFirst = false;
    private ScoreApiRes testCase_1;
    private ScoreApiRes testCase_2;
    private ScoreApiRes testCase_3;
}
