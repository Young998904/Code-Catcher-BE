package com.lion.codecatcherbe.domain.bookmark;

import com.lion.codecatcherbe.domain.bookmark.model.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookmarkRepository extends MongoRepository<Bookmark, String> {

    Optional<Bookmark> findByUserIdAndProblemId(String userId, Long problemId);

    List<Bookmark> findByIdIn(List<String> bookmarkList);

    void deleteAllByUserId(String userId);
}
