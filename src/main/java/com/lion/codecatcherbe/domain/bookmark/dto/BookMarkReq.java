package com.lion.codecatcherbe.domain.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookMarkReq {
    private Long problemId;
    private String codeType;
    private String code;
}
