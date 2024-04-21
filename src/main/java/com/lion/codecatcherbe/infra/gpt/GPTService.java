package com.lion.codecatcherbe.infra.gpt;

import com.google.gson.Gson;
import com.lion.codecatcherbe.domain.coding.dto.response.ProblemGenRes;
import com.lion.codecatcherbe.domain.coding.model.Problem;
import com.lion.codecatcherbe.domain.coding.repository.ProblemRepository;
import com.lion.codecatcherbe.domain.coding.service.SequenceGeneratorService;
import com.lion.codecatcherbe.domain.user.UserService;
import com.lion.codecatcherbe.infra.gpt.dto.request.CodeReviewReq;
import com.lion.codecatcherbe.infra.gpt.dto.response.GPTReviewRes;
import com.lion.codecatcherbe.infra.gpt.prompt.CustomAzureOpenAiClient;
import com.lion.codecatcherbe.infra.gpt.prompt.ReviewPrompt;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.messages.Message;
import org.springframework.ai.prompt.messages.SystemMessage;
import org.springframework.ai.prompt.messages.UserMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GPTService {

    private final SequenceGeneratorService sequenceGeneratorService;
    private final UserService userService;
    private final ProblemRepository problemRepository;
    private final CustomAzureOpenAiClient client;

    public ResponseEntity<GPTReviewRes> getGptFeedback(String token, CodeReviewReq codeReviewReq, String model) {
        String myCode = codeReviewReq.getMyCode();
        Long problemId = codeReviewReq.getProblemId();

        Problem problem = problemRepository.findById(problemId).orElse(null);

        // role : user content : myCode
        UserMessage userMessage = new UserMessage(myCode);

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

        SystemMessage systemMessage = new SystemMessage(systemContent);

        // GPT 에 보낼 Prompt 생성
        List<Message> messages = new ArrayList<>();
        messages.add(userMessage);
        messages.add(systemMessage);

        Prompt prompt = new Prompt(messages);

        // GPT 요청 및 응답 파싱
        int maxRetries = 5; // 최대 호출 가능 횟수
        int retryCount = 0; // 재호출 횟수

        client.setModel(model); // 모델 설정
        Gson gson = new Gson();

        GPTReviewRes gptReviewRes = null;

        while (retryCount < maxRetries) { // 최대 호출 횟수는 5번으로 제한
            String jsonString = client.generate(prompt).getGeneration().getText();

            // gpt4 모델 사용시 json 형태 변환 진행
            if (model.equals("code-catcher-ai-gpt4-preview")) {
                jsonString = jsonString.replace("```json", "").replace("```", "").trim();
            }

            // 원하는 형태로 json 파싱 실패 시 재호출
            try {
                gptReviewRes = gson.fromJson(jsonString, GPTReviewRes.class);
                userService.isUsedToTrue(token);
                break;
            } catch (Exception e) {
                retryCount++;
            }
        }

        if (gptReviewRes == null && retryCount >= maxRetries) {
            // 오류 발생 시 500 에러 반환
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 객체 반환
        return new ResponseEntity<>(gptReviewRes, HttpStatus.OK);
    }

    public HttpStatus getGptProblem(String subject, Long level) {
        // role : user content : myCode
        UserMessage userMessage = new UserMessage(subject + "를 주제로한 코딩 테스트 문제");

        // role : system content : 출력 형식 & 조건
        SystemMessage systemMessage = new SystemMessage("""
            #목표
                이 GPT는 주제에 맞게 코딩테스트 문제를 생성하여 json 형태로 반환합니다.

            #출력 형식
            {
              "title": "문제의 제목",
              "script": "이 문제는 다음과 같습니다.\n첫 번째 줄에는 ...",
              "input_condition": "입력 형태",
              "output_condition": "출력 형태",
              "input_1": "첫 번째 입력 예시.",
              "output_1": "첫 번째 출력 예시.",
              "input_2": "두 번째 입력 예시.",
              "output_2": "두 번째 출력 예시.",
              "input_3": "세 번째 입력 예시.",
              "output_3": "세 번째 출력 예시.",
              "python_code": "파이썬 정답 코드",
              "java_code": "자바 코드 정답 코드",
              "python_explain": "파이썬 코드에 대한 설명.",
              "java_explain": "자바 코드에 대한 설명."
            }

            # 조건
                1. Python 코드는 함수 정의 없이 입력을 바로 받는 형태로 시작하며, Java 코드는 'public class Main {\n    public static void main(String[] args) {\n        }\n}'을 기본 틀로 사용합니다. Java 코드에는 반드시 필요한 import 구문이 포함되어야 합니다. 모든 줄바꿈을 '\n'으로 표현합니다.
                2. 2-3줄에 한번씩 줄바꿈을 \\n 으로 표현합니다.
                3. 입출력 예시와 Python, Java 코드를 제외한 나머지의 출력 언어는 한국어 입니다.
                4. 입력 출력 예시는 표준 입출력을 따르며 줄바꿈은 \\n 으로 표현 합니다.
            """);

        // GPT 에 보낼 Prompt 생성
        List<Message> messages = new ArrayList<>();
        messages.add(userMessage);
        messages.add(systemMessage);

        Prompt p = new Prompt(messages);

        // GPT 요청 및 응답 파싱
        String jsonString = client.generate(p).getGeneration().getText();
        jsonString = jsonString.replace("```json", "").replace("```", "").trim();

        Gson gson = new Gson();

        ProblemGenRes problemGenRes = gson.fromJson(jsonString, ProblemGenRes.class);

        // 문제 저장
        Problem genProblem = Problem.builder()
            .subject(subject)
            .level(level)
            .title(problemGenRes.getTitle())
            .script(problemGenRes.getScript())
            .input_condition(problemGenRes.getInput_condition())
            .output_condition(problemGenRes.getOutput_condition())
            .input_1(problemGenRes.getInput_1())
            .output_1(problemGenRes.getOutput_1())
            .input_2(problemGenRes.getInput_2())
            .output_2(problemGenRes.getOutput_2())
            .input_3(problemGenRes.getInput_3())
            .output_3(problemGenRes.getOutput_3())
            .python_code(problemGenRes.getPython_code())
            .java_code(problemGenRes.getJava_code())
            .python_explain(problemGenRes.getPython_explain())
            .java_explain(problemGenRes.getJava_explain())
            .build();

        genProblem.setCreatedAt(LocalDateTime.now().plusHours(9L));
        genProblem.setId(sequenceGeneratorService.generateSequence(Problem.SEQUENCE_NAME));

        problemRepository.save(genProblem);

        return HttpStatus.CREATED;
    }
}
