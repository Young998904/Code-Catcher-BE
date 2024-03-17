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
public class BookmarkMoreInfoRes {
    List<BookmarkMoreInfo> questionData;
    int totalPage;
    int currentPage;
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class BookmarkMoreInfo {
        private String bookmarkId;
        private Long level;
        private Long problemId;
        private String title;
        private String subject;
        private Date createdAt;
    }
}
