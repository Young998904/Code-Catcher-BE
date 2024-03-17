package com.lion.codecatcherbe.domain.mypage.dto;

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
    List<BookmarkInfo> bookmarkInfo;
    List<ProblemInfo> problemInfo;
    List<Achievement> achieveInfo;
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProblemInfo {
        private Long level;
        private Long problemId;
        private String title;
        private Date createdAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkInfo {
        private String bookmarkId;
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
