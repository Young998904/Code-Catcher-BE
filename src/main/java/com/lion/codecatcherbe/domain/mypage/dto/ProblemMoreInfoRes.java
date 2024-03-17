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
public class ProblemMoreInfoRes {
    List<ProblemMoreInfo> questionData;
    int totalPage;
    int currentPage;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProblemMoreInfo {
        private Long level;
        private Long problemId;
        private String title;
        private String subject;
        private Date createdAt;
        private Boolean status;
    }
}
