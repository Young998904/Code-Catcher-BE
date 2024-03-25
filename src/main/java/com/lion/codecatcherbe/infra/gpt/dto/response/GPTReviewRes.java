package com.lion.codecatcherbe.infra.gpt.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GPTReviewRes {
    private String time;
    private String memory;
    private String suggest;
}
