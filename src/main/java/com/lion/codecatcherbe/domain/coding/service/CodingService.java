package com.lion.codecatcherbe.domain.coding.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.codecatcherbe.domain.coding.dto.response.QuestionRes;
import com.lion.codecatcherbe.domain.coding.model.Problem;
import com.lion.codecatcherbe.domain.coding.repository.ProblemRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class CodingService {
    private final SequenceGeneratorService sequenceGeneratorService;
    private final ProblemRepository problemRepository;
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
}
