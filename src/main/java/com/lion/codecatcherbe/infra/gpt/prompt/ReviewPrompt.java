package com.lion.codecatcherbe.infra.gpt.prompt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewPrompt {
    private String title;
    private String script;
    private String input_condition;
    private String output_condition;
    private String input;
    private String output;

    @Override
    public String toString() {
        return
            "# 주어진 졍보 \n" +
                "제목 : " + title + "\n" +
                "설명 : " + script + "\n" +
                "입력조건 : " + input_condition + "\n" +
                "출력조건 : " + output_condition + "\n" +
                "입력 예시 : " + input + "\n" +
                "출력 예시 : " + output + "\n" +
                "# 목표 \n" +
                "주어진 정보인 문제 상황, 입력/출력 조건, 그리고 예제 하나를 바탕으로 사용자가 보내온 코드 솔루션의 시간 및 메모리 효율성을 분석, 평가하고 개선방안을 반드시 json 형태로 반환합니다.\n"
                +
                "# 출력 형식 \n" +
                "{\n" +
                "\"time\" : \"시간 효율성 평가\",\n" +
                "\"memory\" : \"메모리 효율설 평가\",\n" +
                "\"suggest\" : \"개선 방안\"\n" +
                "}" +
                "# 조건 \n" +
                "시간 효율성 평가는 알고리즘의 시간 복잡도를 포함하여 상세히 설명합니다.\n" +
                "메모리 효율성 평가는 메모리 사용량을 바탕으로 상세히 설명합니다.\n" +
                "2-3줄에 한번씩 줄바꿈을 \\n 으로 표현합니다.\n" +
                "출력 언어는 한국어 입니다."
            ;
    }
}
