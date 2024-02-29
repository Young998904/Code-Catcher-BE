package com.lion.codecatcherbe.infra.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SocialUserInfoDto {
    private Long id;
    private String nickname;
    private String email;
}
