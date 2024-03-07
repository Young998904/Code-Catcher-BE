package com.lion.codecatcherbe.domain.coding;

import com.lion.codecatcherbe.domain.coding.dto.request.QuestionGenReq;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionRes;
import com.lion.codecatcherbe.domain.coding.service.CodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coding")
public class CodingController {

    private final CodingService codingService;

    @GetMapping("/question")
    public ResponseEntity<QuestionRes> getQuestion (@RequestParam String id) {
        QuestionRes question = QuestionRes.builder()
            .title("프린터 큐")
            .subject("Queue(큐)")
            .script(
                "여러분은 프린터의 작업 대기열을 관리하는 프로그램을 작성하고 있습니다. 각 프린트 작업에는 우선순위가 주어지며, 더 높은 우선순위의 작업을 먼저 인쇄해야 합니다. 우선순위는 1에서 9까지의 숫자로 표현되며, 숫자가 클수록 우선순위가 높습니다. 현재 대기열의 상태와 특정 작업의 위치가 주어졌을 때, 그 작업이 인쇄되기까지 얼마나 많은 시간이 걸리는지 계산하세요.")
            .input_condition(
                "첫 번째 줄에는 작업의 개수와 참조하고자 하는 작업의 위치가 주어집니다. 두 번째 줄에는 대기열에 있는 각 작업의 우선순위가 주어집니다.")
            .output_condition("주어진 작업이 인쇄될 때까지 걸리는 시간(대기열에서의 위치 변경 횟수)을 반환합니다.")
            .input_1("3 0\n3 1 4\n")
            .output_1("2")
            .input_2("5 2\n1 1 9 1 1\n")
            .output_2("1")
            .input_3("4 2\n4 2 1 3\n")
            .output_3("2")
            .build();

        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    @PostMapping("/generate")
    public HttpStatus saveProblem (@RequestBody QuestionGenReq questionGenReq) {
        return codingService.saveProblem(questionGenReq.getContent());
    }
}
