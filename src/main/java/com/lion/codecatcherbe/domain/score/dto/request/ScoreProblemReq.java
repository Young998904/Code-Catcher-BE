package com.lion.codecatcherbe.domain.score.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreProblemReq {
    private Long problemId;
    private String codeType;
    private String code;
}
