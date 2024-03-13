package com.lion.codecatcherbe.domain.user.model;

import lombok.Builder;
import lombok.Builder.Default;
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
    @Default
    private int level = 1;

    @Default
    private int exp = 0;

    public void setUserName (String newName) {
        this.name = newName;
    }

    public void setLevAndExp (int level, int exp) {
        this.level = level;
        this.exp = exp;
    }
}
