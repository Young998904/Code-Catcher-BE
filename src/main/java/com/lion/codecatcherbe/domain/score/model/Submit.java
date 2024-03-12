package com.lion.codecatcherbe.domain.score.model;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@Document(collection = "submit")
public class Submit {
    @Id
    private String id;

    private String userId;
    private Long problemId;
    @Default
    private boolean isSuccess = false;
    @Default
    private String lastSubmitJavaCode = null;
    @Default
    private String lastSubmitPythonCode = null;

    public void setLastSubmitJavaCode (String code) {
        this.lastSubmitJavaCode = code;
    }

    public void setLastSubmitPythonCode (String code) {
        this.lastSubmitPythonCode = code;
    }

    public void toggleToSuccess() {
        this.isSuccess = true;
    }
}
