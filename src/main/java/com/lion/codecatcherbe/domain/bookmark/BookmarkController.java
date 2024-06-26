package com.lion.codecatcherbe.domain.bookmark;

import com.lion.codecatcherbe.domain.bookmark.dto.request.BookMarkUpdateReq;
import com.lion.codecatcherbe.domain.bookmark.dto.response.BookMarkDeleteRes;
import com.lion.codecatcherbe.domain.bookmark.dto.response.BookMarkInfoRes;
import com.lion.codecatcherbe.domain.bookmark.dto.request.BookMarkSaveReq;
import com.lion.codecatcherbe.domain.bookmark.dto.response.BookMarkRecordRes;
import com.lion.codecatcherbe.domain.mypage.dto.BookmarkMoreInfoRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
public class BookmarkController {
    private final BookmarkService bookmarkService;
    @PostMapping()
    public ResponseEntity<String> saveBookmark(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody BookMarkSaveReq bookMarkSaveReq) {
        return bookmarkService.saveBookmark(token, bookMarkSaveReq);
    }

    @GetMapping()
    public ResponseEntity<BookMarkInfoRes> findBookmark(@RequestHeader(value = "Authorization", required = false) String token, @RequestParam (name = "id") String bookmarkId) {
        return bookmarkService.findBookmarkInfo(token, bookmarkId);
    }

    @GetMapping("/record")
    public ResponseEntity<BookMarkRecordRes> findBookmarkRecord (@RequestHeader(value = "Authorization", required = false) String token, @RequestParam (name = "problemId") Long problemId) {
        return bookmarkService.findBookmarkRecord(token, problemId);
    }

    @DeleteMapping("/one")
    public HttpStatus deleteOneBookmark (@RequestHeader(value = "Authorization", required = false) String token, @RequestParam (name = "id") String bookmarkId) {
        return bookmarkService.deleteOneBookmark(token, bookmarkId);
    }

    @DeleteMapping("/many")
    public ResponseEntity<BookmarkMoreInfoRes> deleteManyBookmark (@RequestHeader(value = "Authorization", required = false) String token, @RequestBody BookMarkDeleteRes bookMarkDeleteRes) {
        return bookmarkService.deleteManyBookmark(token, bookMarkDeleteRes);
    }

    @PatchMapping()
    public HttpStatus updateBookmark(@RequestHeader(value = "Authorization", required = false) String token, @RequestParam (name="id") String bookmarkId, @RequestBody BookMarkUpdateReq bookMarkUpdateReq) {
        return bookmarkService.updateBookmark(token, bookmarkId, bookMarkUpdateReq);
    }
}
