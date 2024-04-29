package com.lion.codecatcherbe.common;

import com.lion.codecatcherbe.domain.score.model.Submit;

public enum CodeType {
    JAVA {
        @Override
        public void applyCode (Submit submit, String code) {
            submit.setLastSubmitJavaCode(code);
        }
    },
    PYTHON {
        @Override
        public void applyCode (Submit submit, String code) {
            submit.setLastSubmitPythonCode(code);
        }
    },
    JS {
        @Override
        public void applyCode (Submit submit, String code) {
            System.out.println("Js 갱신");
            submit.setLastSubmitJsCode(code);
        }
    }
    ;

    public abstract void applyCode(Submit submit, String code);
}
