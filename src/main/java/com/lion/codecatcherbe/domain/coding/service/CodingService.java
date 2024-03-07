package com.lion.codecatcherbe.domain.coding.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.codecatcherbe.domain.coding.model.Problem;
import com.lion.codecatcherbe.domain.coding.repository.ProblemRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        Problem problem = null;

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
}
