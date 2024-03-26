package com.lion.codecatcherbe.infra.gpt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GPTReviewRes {
    private String time;
    private String memory;
    private String suggest;
}
