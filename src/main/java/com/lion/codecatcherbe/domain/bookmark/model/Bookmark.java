package com.lion.codecatcherbe.domain.bookmark.model;

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
}
