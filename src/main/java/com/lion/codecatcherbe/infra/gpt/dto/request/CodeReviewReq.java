package com.lion.codecatcherbe.infra.gpt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CodeReviewReq {
    private String myCode;
    private Long problemId;
}
