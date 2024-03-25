package com.lion.codecatcherbe.infra.gpt;

import com.google.gson.Gson;
import com.lion.codecatcherbe.domain.coding.model.Problem;
import com.lion.codecatcherbe.domain.coding.repository.ProblemRepository;
import com.lion.codecatcherbe.infra.gpt.dto.request.CodeReviewReq;
import com.lion.codecatcherbe.infra.gpt.dto.response.GPTReviewRes;
import com.lion.codecatcherbe.infra.gpt.prompt.ReviewPrompt;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.client.AiClient;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.messages.Message;
import org.springframework.ai.prompt.messages.SystemMessage;
import org.springframework.ai.prompt.messages.UserMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GPTService {

    private final ProblemRepository problemRepository;
    private final AiClient client;
    public ResponseEntity<GPTReviewRes> getGptFeedback(CodeReviewReq codeReviewReq) {
        String myCode = codeReviewReq.getMyCode();
        Long problemId = codeReviewReq.getProblemId();

        Problem problem = problemRepository.findById(problemId).orElse(null);

        // role : user content : myCode
        UserMessage userMessage = new UserMessage (myCode);

        // role : system content : 문제 정보 & 출력 형식
        ReviewPrompt reviewPrompt = ReviewPrompt.builder()
            .title(problem.getTitle())
            .script(problem.getScript())
            .input_condition(problem.getInput_condition())
            .output_condition(problem.getOutput_condition())
            .input(problem.getInput_1())
            .output(problem.getOutput_1())
            .build();

        String systemContent = reviewPrompt.toString();

        SystemMessage systemMessage  = new SystemMessage(systemContent);

        // GPT 에 보낼 Prompt 생성
        List<Message> messages = new ArrayList<>();
        messages.add(userMessage);
        messages.add(systemMessage);

        Prompt prompt = new Prompt(messages);

        // GPT 요청 및 응답 파싱
        String jsonString = client.generate(prompt).getGeneration().getText();
        jsonString = jsonString.replace("```json", "").replace("```", "").trim();

        Gson gson = new Gson();

        GPTReviewRes gptReviewRes = gson.fromJson(jsonString, GPTReviewRes.class);

        // 객체 반환
        return new ResponseEntity<>(gptReviewRes, HttpStatus.OK);
    }
}
