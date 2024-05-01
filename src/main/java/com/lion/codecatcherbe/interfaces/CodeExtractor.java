package com.lion.codecatcherbe.interfaces;

import com.lion.codecatcherbe.domain.coding.dto.response.GPTFeedBackResultRes;
import com.lion.codecatcherbe.domain.coding.model.Problem;
import com.lion.codecatcherbe.domain.user.model.User;

@FunctionalInterface
public interface CodeExtractor {
    GPTFeedBackResultRes extract (Problem p, User user);
}
