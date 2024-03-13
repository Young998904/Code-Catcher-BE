package com.lion.codecatcherbe.domain.user.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@Document(collection = "achieve")
public class Achieve {
    @Id
    private String id;

    private String userId;
    private LocalDateTime createdAt;
    @Default
    private int cnt = 0;

    public void setCnt(int cnt){
        this.cnt = cnt;
    }
}
