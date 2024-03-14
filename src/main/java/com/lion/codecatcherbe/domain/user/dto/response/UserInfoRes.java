package com.lion.codecatcherbe.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoRes {
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
