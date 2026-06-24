package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.dto.response.BookmarkDTO;
import com.novel.entity.Bookmark;
import com.novel.entity.Category;
import com.novel.entity.Novel;
import com.novel.mapper.BookmarkMapper;
import com.novel.service.BookmarkService;
import com.novel.service.CategoryService;
import com.novel.service.NovelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookmarkServiceImpl extends ServiceImpl<BookmarkMapper, Bookmark> implements BookmarkService {
    
    private final NovelService novelService;
    
    private final CategoryService categoryService;
    
    public BookmarkServiceImpl(NovelService novelService, CategoryService categoryService) {
        this.novelService = novelService;
        this.categoryService = categoryService;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bookmark(Long userId, Long novelId) {
        if (isBookmarked(userId, novelId)) {
            return;
        }
        
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(userId);
        bookmark.setNovelId(novelId);
        save(bookmark);
        
        // 更新小说收藏数
        updateNovelBookmarkCount(novelId, 1);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbookmark(Long userId, Long novelId) {
        LambdaQueryWrapper<Bookmark> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookmark::getUserId, userId)
               .eq(Bookmark::getNovelId, novelId);
        remove(wrapper);
        
        // 更新小说收藏数
        updateNovelBookmarkCount(novelId, -1);
    }
    
    @Override
    public List<Bookmark> getUserBookmarks(Long userId) {
        LambdaQueryWrapper<Bookmark> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookmark::getUserId, userId)
               .orderByDesc(Bookmark::getCreateTime);
        return list(wrapper);
    }
    
    @Override
    public boolean isBookmarked(Long userId, Long novelId) {
        LambdaQueryWrapper<Bookmark> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookmark::getUserId, userId)
               .eq(Bookmark::getNovelId, novelId);
        return count(wrapper) > 0;
    }
    
    private void updateNovelBookmarkCount(Long novelId, int delta) {
        novelService.update(new LambdaUpdateWrapper<Novel>()
            .setSql("bookmark_count = bookmark_count + " + delta)
            .eq(Novel::getNovelId, novelId));
    }
    
    @Override
    public List<BookmarkDTO> getUserBookmarkDetails(Long userId) {
        // 获取用户的收藏列表
        List<Bookmark> bookmarks = getUserBookmarks(userId);
        if (bookmarks.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有小说ID
        List<Long> novelIds = bookmarks.stream()
            .map(Bookmark::getNovelId)
            .collect(Collectors.toList());
        
        // 批量查询小说信息
        LambdaQueryWrapper<Novel> novelWrapper = new LambdaQueryWrapper<>();
        novelWrapper.in(Novel::getNovelId, novelIds);
        List<Novel> novels = novelService.list(novelWrapper);
        
        // 构建小说ID到小说对象的映射
        Map<Long, Novel> novelMap = novels.stream()
            .collect(Collectors.toMap(Novel::getNovelId, n -> n));
        
        // 获取所有分类
        List<Category> categories = categoryService.list();
        Map<Integer, String> categoryMap = categories.stream()
            .collect(Collectors.toMap(Category::getCategoryId, Category::getName));
        
        // 组装DTO
        return bookmarks.stream()
            .map(bookmark -> {
                Novel novel = novelMap.get(bookmark.getNovelId());
                if (novel == null) {
                    return null;
                }
                
                BookmarkDTO dto = new BookmarkDTO();
                dto.setBookmarkId(bookmark.getBookmarkId());
                dto.setNovelId(novel.getNovelId());
                dto.setTitle(novel.getTitle());
                dto.setAuthor(novel.getAuthor());
                dto.setCover(novel.getCover());
                dto.setCategoryName(categoryMap.get(novel.getCategoryId()));
                dto.setDescription(novel.getDescription());
                dto.setStatus(novel.getStatus());
                dto.setWordCount(novel.getWordCount());
                dto.setCreateTime(bookmark.getCreateTime());
                return dto;
            })
            .filter(dto -> dto != null)
            .collect(Collectors.toList());
    }
}
