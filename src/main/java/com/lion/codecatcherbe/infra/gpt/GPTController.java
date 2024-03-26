package com.lion.codecatcherbe.infra.gpt;

import com.lion.codecatcherbe.infra.gpt.dto.request.CodeReviewReq;
import com.lion.codecatcherbe.infra.gpt.dto.response.GPTReviewRes;
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
    public ResponseEntity<GPTReviewRes> gptFeedbackTest (@RequestBody CodeReviewReq codeReviewReq) {

        return gptService.getGptFeedback(codeReviewReq);
    }
}
