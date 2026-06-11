package com.novel.controller;

import com.novel.common.Result;
import com.novel.entity.Bookmark;
import com.novel.service.BookmarkService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/novel")
public class BookmarkController {
    
    private final BookmarkService bookmarkService;
    
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }
    
    /**
     * 收藏小说
     */
    @PostMapping("/{novelId}/bookmark")
    public Result<Void> bookmark(@PathVariable Long novelId, Principal principal) {
        Long userId = getCurrentUserId(principal);
        bookmarkService.bookmark(userId, novelId);
        return Result.success();
    }
    
    /**
     * 取消收藏
     */
    @DeleteMapping("/{novelId}/bookmark")
    public Result<Void> unbookmark(@PathVariable Long novelId, Principal principal) {
        Long userId = getCurrentUserId(principal);
        bookmarkService.unbookmark(userId, novelId);
        return Result.success();
    }
    
    /**
     * 获取用户的收藏列表
     */
    @GetMapping("/user/bookmarks")
    public Result<List<Bookmark>> getUserBookmarks(Principal principal) {
        Long userId = getCurrentUserId(principal);
        List<Bookmark> bookmarks = bookmarkService.getUserBookmarks(userId);
        return Result.success(bookmarks);
    }
    
    private Long getCurrentUserId(Principal principal) {
        // TODO: 从 principal 中获取用户 ID
        return 1L;
    }
}
