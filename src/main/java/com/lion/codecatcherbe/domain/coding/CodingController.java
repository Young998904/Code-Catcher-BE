package com.lion.codecatcherbe.domain.coding;

import com.lion.codecatcherbe.domain.coding.dto.request.QuestionGenReq;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionListRes;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionListRes.QuestionInfo;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionRes;
import com.lion.codecatcherbe.domain.coding.service.CodingService;
import com.lion.codecatcherbe.domain.coding.dto.response.GPTFeedBackResultRes;
import com.lion.codecatcherbe.domain.coding.dto.response.ProblemGenRes;
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
@RequestMapping("/coding")
public class CodingController {

    private final CodingService codingService;

    @GetMapping("/question")
    public ResponseEntity<QuestionRes> findQuestion (@RequestHeader(value = "Authorization", required = false) String token, @RequestParam Long id) {
        return codingService.findProblem(token, id);
    }

    @PostMapping("/generate")
    public HttpStatus saveProblem (@RequestBody QuestionGenReq questionGenReq) {
        return codingService.saveProblem(questionGenReq.getContent());
    }

    @GetMapping("/questionlist")
    public ResponseEntity<QuestionListRes> findQuestionList(@RequestHeader(value = "Authorization", required = false) String token) {
        return codingService.findProblemList(token);
    }

    @GetMapping("/gpt/feedback")
    public ResponseEntity<GPTFeedBackResultRes> getGPTFeedBack(@RequestHeader(value = "Authorization", required = false) String token, @RequestParam Long problemId, @RequestParam String codeType) {
        return codingService.getGPTCode(token, problemId, codeType);
    }

    @PostMapping("gen")
    public HttpStatus genProblem(@RequestBody ProblemGenRes problemGenRes) {
        return codingService.genProblem(problemGenRes);
    }
}
