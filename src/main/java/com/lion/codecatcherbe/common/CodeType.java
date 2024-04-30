package com.lion.codecatcherbe.common;

import com.lion.codecatcherbe.domain.coding.model.Problem;
import com.lion.codecatcherbe.domain.score.model.Submit;

public enum CodeType {
    JAVA {
        @Override
        public void applyCode (Submit submit, String code) {
            submit.setLastSubmitJavaCode(code);
        }
        @Override
        public String[] extractCode (Problem problem) {
            String code = problem.getJava_code();
            String explain = problem.getJava_explain();
            return new String[] {code, explain};
        }
    },
    PYTHON {
        @Override
        public void applyCode (Submit submit, String code) {
            submit.setLastSubmitPythonCode(code);
        }
        @Override
        public String[] extractCode (Problem problem) {
            String code = problem.getPython_code();
            String explain = problem.getPython_explain();
            return new String[] {code, explain};
        }
    },
    JS {
        @Override
        public void applyCode (Submit submit, String code) {submit.setLastSubmitJsCode(code);}
        @Override
        public String[] extractCode (Problem problem) {
            String code = problem.getJs_code();
            String explain = problem.getJs_explain();
            return new String[] {code, explain};
        }
    }
    ;

    public abstract void applyCode(Submit submit, String code);
    public abstract String[] extractCode(Problem problem);
}
