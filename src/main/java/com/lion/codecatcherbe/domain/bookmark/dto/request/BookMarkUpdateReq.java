package com.lion.codecatcherbe.domain.bookmark.dto.request;

import com.lion.codecatcherbe.infra.gpt.dto.response.GPTReviewRes;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookMarkUpdateReq {
    private String myCode;
    private String codeType;
    private GPTReviewRes gptReview;

    public Optional<GPTReviewRes> getGptReview () {
        return Optional.ofNullable(gptReview);
    }
}
