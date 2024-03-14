package com.lion.codecatcherbe.domain.mypage;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MyPageInfoRes {
    List<Info> bookmarkInfo;
    List<Info> problemInfo;
    List<Achievement> achieveInfo;
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private Long level;
        private Long problemId;
        private String title;
        private Date createdAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Achievement {
        private Date createdAt;
        private int cnt;
    }
}
