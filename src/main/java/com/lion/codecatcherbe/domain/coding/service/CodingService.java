package com.lion.codecatcherbe.domain.coding.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionListRes;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionListRes.QuestionInfo;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionRes;
import com.lion.codecatcherbe.domain.coding.model.Problem;
import com.lion.codecatcherbe.domain.coding.repository.ProblemRepository;
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
    private final MongoOperations mongoOperations;
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

    public ResponseEntity<QuestionRes> findProblem(Long id) {
        Problem problem = problemRepository.findById(id).orElse(null);

        if (problem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        QuestionRes questionRes = QuestionRes.builder()
            .title(problem.getTitle())
            .subject(problem.getSubject())
            .script(problem.getScript())
            .input_condition(problem.getInput_condition())
            .output_condition(problem.getOutput_condition())
            .input_1(problem.getInput_1())
            .output_1(problem.getOutput_1())
            .input_2(problem.getOutput_2())
            .output_2(problem.getOutput_2())
            .input_3(problem.getInput_3())
            .output_3(problem.getOutput_3())
            .build();

        return new ResponseEntity<>(questionRes, HttpStatus.OK);
    }

    public ResponseEntity<QuestionListRes> findProblemList() {
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusHours(9L);
        List<Problem> problemList = findProblemsByCreatedAt(start, start.plusHours(24L));

        problemList.sort(Comparator.comparingLong(Problem::getLevel));

        Problem p1 = problemList.get(0);
        Problem p2 = problemList.get(1);
        Problem p3 = problemList.get(2);

        QuestionListRes questionListRes = QuestionListRes.builder()
            .question_1(new QuestionInfo(p1.getId(), p1.getLevel(), p1.getTitle(), p1.getSubject(), p1.getScript()))
            .question_2(new QuestionInfo(p2.getId(), p2.getLevel(), p2.getTitle(), p2.getSubject(), p2.getScript()))
            .question_3(new QuestionInfo(p3.getId(), p3.getLevel(), p3.getTitle(), p3.getSubject(), p3.getScript()))
            .build();

        return new ResponseEntity<>(questionListRes, HttpStatus.OK);
    }

    public List<Problem> findProblemsByCreatedAt(LocalDateTime start, LocalDateTime end) {
        // 시작 날짜와 종료 날짜 설정

        Query query = new Query(Criteria.where("createdAt").gt(start).lt(end));

//        System.out.println(query);
        return mongoOperations.find(query, Problem.class);
    }
}
