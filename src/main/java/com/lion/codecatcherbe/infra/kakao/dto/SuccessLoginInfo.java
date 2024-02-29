package com.lion.codecatcherbe.infra.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class SuccessLoginInfo {
    private String jwt;
    private Long userId;
    private String nickname;
}
