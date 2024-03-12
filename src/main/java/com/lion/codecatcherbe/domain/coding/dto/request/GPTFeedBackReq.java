package com.lion.codecatcherbe.domain.coding.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GPTFeedBackReq {
    private Long problemId;
    private String codeType;
}
