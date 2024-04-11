package com.lion.codecatcherbe.domain.bookmark.dto.response;

import com.lion.codecatcherbe.infra.gpt.dto.response.GPTReviewRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookMarkInfoRes {
    // Bookmark 에서 가져올 값
    private String myCode;
    private String codeType;
    private GPTReviewRes gptReviewRes;

    // Problem 에서 가져올 값
    private Long level;
    private String title;
    private String subject;
    private String script;
    private String input_condition;
    private String output_condition;
    private String input_1;
    private String output_1;
    private String input_2;
    private String output_2;
    private String gptCode;
    private String gptExplain;
}
