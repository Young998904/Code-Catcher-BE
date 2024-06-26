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
    private Boolean isSuccess = false;
    @Default
    private String lastSubmitJavaCode = null;
    @Default
    private String lastSubmitPythonCode = null;
    @Default
    private String lastSubmitJsCode = null;

    public void setLastSubmitJavaCode (String code) {
        this.lastSubmitJavaCode = code;
    }

    public void setLastSubmitPythonCode (String code) {
        this.lastSubmitPythonCode = code;
    }

    public void setLastSubmitJsCode (String code) {this.lastSubmitJsCode = code;}

    public void toggleToSuccess() {
        this.isSuccess = true;
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }
}
