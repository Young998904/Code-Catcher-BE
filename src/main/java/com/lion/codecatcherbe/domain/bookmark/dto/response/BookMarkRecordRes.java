package com.lion.codecatcherbe.domain.bookmark.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookMarkRecordRes {
    private String bookmarkId;
    private LocalDateTime createdAt;
}
