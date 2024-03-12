package com.lion.codecatcherbe.domain.coding.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GPTFeedBackResultRes {
    private String gptCode;
    private String gptCodeExplain;
}
