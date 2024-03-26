package com.lion.codecatcherbe.domain.coding.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.codecatcherbe.domain.coding.dto.response.GPTFeedBackResultRes;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionListRes;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionListRes.QuestionInfo;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionRes;
import com.lion.codecatcherbe.domain.coding.model.Problem;
import com.lion.codecatcherbe.domain.coding.repository.ProblemRepository;
import com.lion.codecatcherbe.domain.score.model.Submit;
import com.lion.codecatcherbe.domain.score.repository.SubmitRepository;
import com.lion.codecatcherbe.domain.coding.dto.response.ProblemGenRes;
import com.lion.codecatcherbe.domain.user.repository.UserRepository;
import com.lion.codecatcherbe.domain.user.model.User;
import com.lion.codecatcherbe.infra.kakao.security.TokenProvider;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class CodingService {
    private final SequenceGeneratorService sequenceGeneratorService;
    private final ProblemRepository problemRepository;
    private final SubmitRepository submitRepository;
    private final UserRepository userRepository;
    private final MongoOperations mongoOperations;

    public String filterJwt (String token) {
        String jwt = null;

        if (token != null && token.startsWith("Bearer ")) {
            jwt = token.substring(7);
        }

        // 토큰 사용하여 필요한 로직 처리
        return jwt;
    }

    public String getUserId(String jwt) {
        String userId;
        try {
            userId = TokenProvider.getSubject(jwt);
        } catch (Exception e) {
            return null;
        }
        return userId;
    }
    @Transactional
    public HttpStatus saveProblem(String content) {
        ObjectMapper mapper = new ObjectMapper();

        Problem problem;

        try {
            problem = mapper.readValue(content, Problem.class);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpStatus.BAD_REQUEST;
        }

        problem.setCreatedAt(LocalDateTime.now().plusHours(9L));
        problem.setId(sequenceGeneratorService.generateSequence(Problem.SEQUENCE_NAME));

        problemRepository.save(problem);

        return HttpStatus.CREATED;
    }

    public ResponseEntity<QuestionRes> findProblem(String token, Long id) {
        // 문제 가져오기
        Problem problem = problemRepository.findById(id).orElse(null);

        if (problem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 유저의 풀이 정보 가져오기
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Submit submit = submitRepository.findByUserIdAndProblemId(userId, id).orElse(null);

        String javaCode, pythonCode;

        if (submit == null) {
            javaCode = null;
            pythonCode = null;
        }
        else {
            javaCode = submit.getLastSubmitJavaCode();
            pythonCode = submit.getLastSubmitPythonCode();
        }


        QuestionRes questionRes = QuestionRes.builder()
            .level(problem.getLevel())
            .title(problem.getTitle())
            .subject(problem.getSubject())
            .script(problem.getScript())
            .input_condition(problem.getInput_condition())
            .output_condition(problem.getOutput_condition())
            .input_1(problem.getInput_1())
            .output_1(problem.getOutput_1())
            .input_2(problem.getOutput_2())
            .output_2(problem.getOutput_2())
            .javaSubmitCode(javaCode)
            .pythonSubmitCode(pythonCode)
            .build();

        return new ResponseEntity<>(questionRes, HttpStatus.OK);
    }

    public ResponseEntity<QuestionListRes> findProblemList(String token) {
        // 오늘 날짜를 가져오고 끝날짜를 24시간 뒤로 설정하여 문제의 정보를 가지고 옴
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        List<Problem> problemList = findProblemsByCreatedAt(start, start.plusHours(24L));

        // 레벨 순으로 정렬
        problemList.sort(Comparator.comparingLong(Problem::getLevel));

        Problem p1 = problemList.get(0);
        Problem p2 = problemList.get(1);
        Problem p3 = problemList.get(2);

        // 유저의 Submit 기록 확인
        String jwt = filterJwt(token);

        String userId = getUserId(jwt);

        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Submit s1 = submitRepository.findIsSuccessByUserIdAndProblemId(user.getId(), p1.getId()).orElse(null);
        Submit s2 = submitRepository.findIsSuccessByUserIdAndProblemId(user.getId(), p2.getId()).orElse(null);
        Submit s3 = submitRepository.findIsSuccessByUserIdAndProblemId(user.getId(), p3.getId()).orElse(null);

        Boolean b1, b2, b3;

        if (s1 == null) b1 = null; else b1 = s1.getIsSuccess();
        if (s2 == null) b2 = null; else b2 = s2.getIsSuccess();
        if (s3 == null) b3 = null; else b3 = s3.getIsSuccess();

        QuestionListRes questionListRes = QuestionListRes.builder()
            .question_1(new QuestionInfo(p1.getId(), b1, p1.getLevel(), p1.getTitle(), p1.getSubject(), p1.getScript()))
            .question_2(new QuestionInfo(p2.getId(), b2, p2.getLevel(), p2.getTitle(), p2.getSubject(), p2.getScript()))
            .question_3(new QuestionInfo(p3.getId(), b3, p3.getLevel(), p3.getTitle(), p3.getSubject(), p3.getScript()))
            .build();

        return new ResponseEntity<>(questionListRes, HttpStatus.OK);
    }

    public List<Problem> findProblemsByCreatedAt(LocalDateTime start, LocalDateTime end) {
        // 시작 날짜와 종료 날짜 설정

        Query query = new Query(Criteria.where("createdAt").gt(start).lt(end));

//        System.out.println(query);
        return mongoOperations.find(query, Problem.class);
    }

    public ResponseEntity<GPTFeedBackResultRes> getGPTCode(Long problemId, String codeType) {
        Problem p = problemRepository.findById(problemId).orElse(null);

        if (p == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (codeType.equals("java")) {
            return new ResponseEntity<>(new GPTFeedBackResultRes(p.getJava_code(), p.getJava_explain()), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(new GPTFeedBackResultRes(p.getPython_code(), p.getPython_explain()), HttpStatus.OK);
        }
    }

    public HttpStatus genProblem(ProblemGenRes problemGenRes) {
        Problem p = Problem.builder()
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

        p.setCreatedAt(LocalDateTime.now().plusHours(9L));
        p.setId(sequenceGeneratorService.generateSequence(Problem.SEQUENCE_NAME));

        problemRepository.save(p);

        return HttpStatus.CREATED;
    }
}
