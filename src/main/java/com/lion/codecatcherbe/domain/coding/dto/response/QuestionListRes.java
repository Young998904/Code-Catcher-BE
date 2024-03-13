package com.lion.codecatcherbe.domain.coding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuestionListRes {

    QuestionInfo question_1;
    QuestionInfo question_2;
    QuestionInfo question_3;
    @AllArgsConstructor
    @Getter
    public static class QuestionInfo {
        private Long id;
        private Boolean isSuccess;
        private Long level;
        private String title;
        private String subject;
        private String script;
    }
}
