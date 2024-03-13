package com.lion.codecatcherbe.domain.user.repository;

import com.lion.codecatcherbe.domain.user.model.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
