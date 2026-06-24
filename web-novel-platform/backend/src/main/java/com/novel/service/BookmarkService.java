package com.novel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.novel.dto.response.BookmarkDTO;
import com.novel.entity.Bookmark;

import java.util.List;

public interface BookmarkService extends IService<Bookmark> {
    
    /**
     * 收藏小说
     */
    void bookmark(Long userId, Long novelId);
    
    /**
     * 取消收藏
     */
    void unbookmark(Long userId, Long novelId);
    
    /**
     * 获取用户的收藏列表
     */
    List<Bookmark> getUserBookmarks(Long userId);
    
    /**
     * 检查是否已收藏
     */
    boolean isBookmarked(Long userId, Long novelId);
    
    /**
     * 获取用户的书架详情列表
     */
    List<BookmarkDTO> getUserBookmarkDetails(Long userId);
}
