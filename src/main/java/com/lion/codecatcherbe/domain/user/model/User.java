package com.lion.codecatcherbe.domain.user.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@Document(collection = "user")
public class User {
    @Id
    private String id;

    private final String password;
    private final Long kakaoId;
    private final String email;

    private String name;
}
