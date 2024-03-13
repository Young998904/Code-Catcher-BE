package com.lion.codecatcherbe.domain.bookmark;

import com.lion.codecatcherbe.domain.bookmark.model.Bookmark;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookmarkRepository extends MongoRepository<Bookmark, String> {

}
