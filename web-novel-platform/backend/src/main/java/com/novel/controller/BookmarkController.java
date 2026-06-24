package com.novel.controller;

import com.novel.common.Result;
import com.novel.dto.response.BookmarkDTO;
import com.novel.entity.Bookmark;
import com.novel.security.JwtUserDetails;
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
    
    /**
     * 获取用户的书架详情列表（包含小说信息）
     */
    @GetMapping("/user/bookmarks/detail")
    public Result<List<BookmarkDTO>> getUserBookmarkDetails(Principal principal) {
        Long userId = getCurrentUserId(principal);
        List<BookmarkDTO> bookmarks = bookmarkService.getUserBookmarkDetails(userId);
        return Result.success(bookmarks);
    }
    
    private Long getCurrentUserId(Principal principal) {
        if (principal instanceof org.springframework.security.core.Authentication) {
            org.springframework.security.core.Authentication auth = 
                (org.springframework.security.core.Authentication) principal;
            Object principalObj = auth.getPrincipal();
            if (principalObj instanceof JwtUserDetails) {
                return ((JwtUserDetails) principalObj).getId();
            }
        }
        // 如果无法从 Principal 中获取用户 ID，返回 null
        return null;
    }
}
