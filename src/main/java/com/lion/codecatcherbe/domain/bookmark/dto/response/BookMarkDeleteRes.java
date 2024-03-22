package com.lion.codecatcherbe.domain.bookmark.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BookMarkDeleteRes {
    private int currentPage;
    private List<String> bookmarkList;
}
