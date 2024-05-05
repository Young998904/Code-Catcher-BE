package com.lion.codecatcherbe.infra.social.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class SuccessLoginInfo {
    private String jwt;
    private boolean isNew;
    private Long userId;
    private String nickname;
    private String email;
    private int level;
    private int exp;
    private int expUpper;
    private int totalCnt;
    private int completeCnt;
    private int bookmarkCnt;
}
