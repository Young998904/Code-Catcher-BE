package com.lion.codecatcherbe.domain.score.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ScoreApiRes {
    private boolean error;
    private String error_message;
    private String input;
    private String expected_output;
    private String actual_output;
    private boolean correct;
}
