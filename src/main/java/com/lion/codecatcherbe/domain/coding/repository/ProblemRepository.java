package com.lion.codecatcherbe.domain.coding.repository;

import com.lion.codecatcherbe.domain.coding.model.Problem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProblemRepository extends MongoRepository<Problem, Long> {

}
