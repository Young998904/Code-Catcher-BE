package com.lion.codecatcherbe.domain.coding;

import com.lion.codecatcherbe.domain.coding.dto.request.QuestionGenReq;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionListRes;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionListRes.QuestionInfo;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionRes;
import com.lion.codecatcherbe.domain.coding.service.CodingService;
import com.lion.codecatcherbe.domain.coding.dto.request.GPTFeedBackReq;
import com.lion.codecatcherbe.domain.coding.dto.response.GPTFeedBackResultRes;
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

    @GetMapping("/mock/question")
    public ResponseEntity<QuestionRes> findMockQuestion (@RequestParam Long id) {
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

    @GetMapping("/question")
    public ResponseEntity<QuestionRes> findQuestion (@RequestParam Long id) {
        return codingService.findProblem(id);
    }

    @PostMapping("/generate")
    public HttpStatus saveProblem (@RequestBody QuestionGenReq questionGenReq) {
        return codingService.saveProblem(questionGenReq.getContent());
    }

    @GetMapping("/mock/questionlist")
    public ResponseEntity<QuestionListRes> findMockQuestionList() {
        QuestionListRes questionListRes = QuestionListRes.builder()
            .question_1(new QuestionInfo(1L, 1L, "숫자 배열의 모든 순열 찾기", "배열",
                "주어진 숫자 배열에서 가능한 모든 순열을 구하는 프로그램을 작성하세요.\n"
                    + "순열이란, 주어진 숫자들을 재배치하여 얻을 수 있는 모든 가능한 배열입니다.\n"
                    + "예를 들어, [1,2,3]의 모든 순열은 [1,2,3], [1,3,2], [2,1,3], [2,3,1], [3,1,2], [3,2,1]입니다."))
            .question_2(new QuestionInfo(2L, 2L, "최소 동전 교환 문제", "동적 프로그래밍",
                "주어진 금액을 만들기 위해 필요한 최소한의 동전 개수를 찾는 프로그램을 작성하세요.\n"
                    + "사용할 수 있는 동전의 종류는 무제한이며, 각 동전의 가치는 주어진 배열에 담겨 있습니다.\n"
                    + "예를 들어, 가치가 1, 2, 5인 동전으로 11원을 만들 때 필요한 최소 동전 개수를 계산해야 합니다."))
            .question_3(new QuestionInfo(3L, 3L, "최대 힙에서의 데이터 조회", "힙",
                "정수들을 저장하고 있는 최대 힙(max heap)이 있습니다. 이 힙에서 k번째로 큰 요소를 조회하는 프로그램을 작성하세요.\n"
                    + "예를 들어, 최대 힙에 [9, 7, 5, 3, 2, 4, 1]이 저장되어 있다면, k=3인 경우, 세 번째로 큰 요소는 5입니다."))
            .build();

        return new ResponseEntity<>(questionListRes, HttpStatus.OK);
    }

    @GetMapping("/questionlist")
    public ResponseEntity<QuestionListRes> findQuestionList() {
        return codingService.findProblemList();
    }

    @GetMapping("/mock/gpt/feedback")
    public ResponseEntity<GPTFeedBackResultRes> getGPTFeedBack(@RequestBody GPTFeedBackReq gptFeedBackReq) {
        GPTFeedBackResultRes gptFeedBackResultRes = GPTFeedBackResultRes.builder()
            .gptCode("def gcd(a, b):\n    while b:\n        a, b = b, a % b\n    return a\n\ndef lcm(a, b):\n    return a * b // gcd(a, b)\n\na, b = map(int, input().split())\nprint(lcm(a, b))")
            .gptCodeExplain("이 파이썬 코드는 최대공약수(gcd)를 유클리드 호제법으로 구한 뒤, 입력받은 두 수의 곱을 최대공약수로 나누어 최소공배수(lcm)를 계산합니다.")
            .build();
        return new ResponseEntity<>(gptFeedBackResultRes, HttpStatus.OK);
    }
}
