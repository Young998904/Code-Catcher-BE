package com.lion.codecatcherbe.domain.bookmark.model;

import com.lion.codecatcherbe.infra.gpt.dto.response.GPTReviewRes;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@Document(collection = "bookmark")
public class Bookmark {
    @Id
    private String id;

    private String userId;
    private Long problemId;
    private LocalDateTime createdAt;
    private String code;
    private String codeType;
    private GPTReviewRes gptReviewRes;

    public void updateBook(LocalDateTime createdAt, String code, String codeType) {
        this.createdAt = createdAt;
        this.code = code;
        this.codeType = codeType;
    }
}
