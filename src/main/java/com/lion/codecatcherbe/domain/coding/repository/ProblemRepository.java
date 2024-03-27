package com.lion.codecatcherbe.domain.coding.repository;

import com.lion.codecatcherbe.domain.coding.model.Problem;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProblemRepository extends MongoRepository<Problem, Long> {

    List<Problem> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
