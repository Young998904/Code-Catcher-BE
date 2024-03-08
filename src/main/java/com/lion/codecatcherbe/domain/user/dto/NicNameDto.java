package com.lion.codecatcherbe.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NicNameDto {
    private String nickname;

    public NicNameDto (String nickname) {
        this.nickname = nickname;
    }
}
