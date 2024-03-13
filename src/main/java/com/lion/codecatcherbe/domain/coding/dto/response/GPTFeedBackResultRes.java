package com.lion.codecatcherbe.domain.coding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GPTFeedBackResultRes {
    private String gptCode;
    private String gptCodeExplain;
}
