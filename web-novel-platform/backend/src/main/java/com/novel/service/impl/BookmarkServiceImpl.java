package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.entity.Bookmark;
import com.novel.mapper.BookmarkMapper;
import com.novel.service.BookmarkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookmarkServiceImpl extends ServiceImpl<BookmarkMapper, Bookmark> implements BookmarkService {
    
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
        // 这里简化处理，实际应该使用 SQL 更新
        // baseMapper.update(null, new LambdaUpdateWrapper<Novel>()
        //     .setSql("bookmark_count = bookmark_count + " + delta)
        //     .eq(Novel::getId, novelId));
    }
}
