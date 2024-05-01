package com.lion.codecatcherbe.domain.coding.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestionRes {
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
    private String javaSubmitCode;
    private String pythonSubmitCode;
    private String jsSubmitCode;
}
