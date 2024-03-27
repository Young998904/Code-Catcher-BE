package com.lion.codecatcherbe.domain.score.repository;

import com.lion.codecatcherbe.domain.score.model.Submit;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SubmitRepository extends MongoRepository<Submit, String> {

    Optional<Submit> findByUserIdAndProblemId(String userId, Long problemId);

    @Query(value="{ 'userId' : ?0, 'problemId' : ?1 }", fields="{ 'isSuccess' : 1, '_id': 0 }")
    Optional<Submit> findIsSuccessByUserIdAndProblemId(String userId, Long problemId);

    void deleteAllByUserId(String userId);
}
