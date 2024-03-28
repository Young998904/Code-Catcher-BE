package com.lion.codecatcherbe.infra.gpt;

import com.lion.codecatcherbe.infra.gpt.dto.request.CodeReviewReq;
import com.lion.codecatcherbe.infra.gpt.dto.response.GPTReviewRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gpt")
public class GPTController {

    private final GPTService gptService;
    /*
        ChatGPT 문제 생성
    */
    @GetMapping("")
    public HttpStatus gptGenTest (@RequestParam String subject, @RequestParam Long level) {
        return gptService.getGptProblem(subject, level);
    }

    /*
        ChatGPT 내 코드 피드백
    */
    @PostMapping("/feedback")
    public ResponseEntity<GPTReviewRes> gptFeedbackTest (@RequestHeader(value = "Authorization", required = false) String token, @RequestBody CodeReviewReq codeReviewReq) {

        return gptService.getGptFeedback(token, codeReviewReq);
    }

    /*
        ChatGPT 내 코드 피드백
    */
    @PostMapping("/mock/feedback")
    public ResponseEntity<GPTReviewRes> gptMockFeedbackTest (@RequestBody CodeReviewReq codeReviewReq) {

        GPTReviewRes gptReviewRes = new GPTReviewRes(
            "사용자의 코드는 각 문자를 한 번씩 순회하면서 스택에 괄호를 추가하거나 제거합니다. \n이 과정은 입력된 문자열의 길이에 선형적으로 비례하여 시간 복잡도는 O(n)입니다. \n주어진 문자열의 길이가 최대 1000이므로, 이 알고리즘은 실제 사용 환경에서도 빠르게 수행될 것으로 예상됩니다.",
            "메모리 사용량은 주로 스택에 괄호를 저장하는 데 사용됩니다. \n스택의 크기는 최악의 경우 입력된 문자열의 길이와 동일해질 수 있으므로, 메모리 사용량은 입력 크기에 비례합니다. \n하지만 입력 크기가 1000으로 제한되어 있으므로, 메모리 사용량도 크게 문제가 되지 않습니다.",
            "현재 코드는 이미 시간과 메모리 측면에서 효율적입니다. \n하지만, 입력된 문자열에 알파벳 소문자가 포함될 수 있으므로, 괄호가 아닌 문자를 만났을 때의 처리 로직을 추가하는 것이 좋습니다. \n이를 통해 괄호가 아닌 문자는 무시하고 괄호에 대해서만 스택 연산을 수행하도록 개선할 수 있습니다."
        );
        return new ResponseEntity<>(gptReviewRes, HttpStatus.OK);
    }
}
