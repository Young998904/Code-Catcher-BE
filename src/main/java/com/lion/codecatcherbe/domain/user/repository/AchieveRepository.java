package com.lion.codecatcherbe.domain.user.repository;

import com.lion.codecatcherbe.domain.user.model.Achieve;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AchieveRepository extends MongoRepository<Achieve, String> {

    Optional<Achieve> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime start, LocalDateTime end);

    void deleteAllByUserId(String userId);
}
