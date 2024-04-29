package com.lion.codecatcherbe.domain.coding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProblemGenRes {
    private String title;
    private String script;
    private String input_condition;
    private String output_condition;
    private String input_1;
    private String output_1;
    private String input_2;
    private String output_2;
    private String input_3;
    private String output_3;
    private String python_code;
    private String java_code;
    private String python_explain;
    private String java_explain;
    private String js_code;
    private String js_explain;
}
