package com.lion.codecatcherbe.domain.coding.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "problem")
public class Problem {
    @Transient
    public static final String SEQUENCE_NAME = "problem_sequence";

    @Setter
    @Id
    private Long id;

    @Setter
    private LocalDateTime createdAt;
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
    private String input_3;
    private String output_3;
    private String python_code;
    private String java_code;
}
