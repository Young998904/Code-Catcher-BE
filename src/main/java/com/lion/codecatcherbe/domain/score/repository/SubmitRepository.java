package com.lion.codecatcherbe.domain.score.repository;

import com.lion.codecatcherbe.domain.score.model.Submit;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubmitRepository extends MongoRepository<Submit, String> {

    Optional<Submit> findByUserIdAndProblemId(String userId, Long problemId);
}
