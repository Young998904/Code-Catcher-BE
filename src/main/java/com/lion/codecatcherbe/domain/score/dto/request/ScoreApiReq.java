package com.lion.codecatcherbe.domain.score.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ScoreApiReq {
    private String code;
    private String input;
    private String output;
}
