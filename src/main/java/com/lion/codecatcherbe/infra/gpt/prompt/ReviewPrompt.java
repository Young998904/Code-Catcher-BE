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

            "## 목표 \n" +
            "##주어진 정보의 설명, 입출력 조건, 그리고 예시를 바탕으로 사용자가 보내온 코드 솔루션의 시간 및 메모리 효율성을 분석, 평가하고 개선방안을 이에 대한 내용을 반드시 출력형식에 맞춰 보냅니다.\n"
            +
            "## 출력 형식 \n" +
            "{\n" +
            "\"time\" : \"해당 알고리즘의 시간 복잡도는 O() 입니다. 시간 복잡도에 대한 상세한 설명\",\n" +
            "\"memory\" : \"해당 알고리즘의 공간 복잡도는 O() 입니다. 공간 복잡도에 대한 상세한 설명\",\n" +
            "\"suggest\" : \"개선 방안에 대한 상세한 피드백\",\n" +
            "}" +

            "## 조건 \n" +
            "반드시 출력 형식에 맞춰서 중괄호 안에 담아서 보냄.\n" +
            "시간 효율성 평가는 알고리즘의 시간 복잡도를 포함하여 3줄로 상세히 설명.\n" +
            "메모리 효율성 평가는 메모리 사용량을 바탕으로 상세히 3줄로 상세히 설명.\n" +
            "2-3줄에 한번씩 줄바꿈을 \\n 으로 표현.\n" +
            "출력 언어는 한국어."
        ;
    }
}
