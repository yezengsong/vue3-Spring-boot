package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.dto.response.ReadHistoryDTO;
import com.novel.entity.Category;
import com.novel.entity.Chapter;
import com.novel.entity.Novel;
import com.novel.entity.ReadHistory;
import com.novel.mapper.ReadHistoryMapper;
import com.novel.service.CategoryService;
import com.novel.service.ChapterService;
import com.novel.service.NovelService;
import com.novel.service.ReadHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReadHistoryServiceImpl extends ServiceImpl<ReadHistoryMapper, ReadHistory> implements ReadHistoryService {
    
    private final NovelService novelService;
    
    private final ChapterService chapterService;
    
    private final CategoryService categoryService;
    
    public ReadHistoryServiceImpl(NovelService novelService, ChapterService chapterService, CategoryService categoryService) {
        this.novelService = novelService;
        this.chapterService = chapterService;
        this.categoryService = categoryService;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordReadHistory(Long userId, Long novelId, Long chapterId) {
        // 查找是否已有该小说的阅读记录
        LambdaQueryWrapper<ReadHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReadHistory::getUserId, userId)
               .eq(ReadHistory::getNovelId, novelId);
        ReadHistory existingHistory = getOne(wrapper);
        
        if (existingHistory != null) {
            // 更新现有记录
            existingHistory.setChapterId(chapterId);
            updateById(existingHistory);
        } else {
            // 创建新记录
            ReadHistory history = new ReadHistory();
            history.setUserId(userId);
            history.setNovelId(novelId);
            history.setChapterId(chapterId);
            save(history);
        }
    }
    
    @Override
    public List<ReadHistoryDTO> getUserReadHistory(Long userId) {
        // 获取用户的阅读历史（按阅读时间倒序）
        LambdaQueryWrapper<ReadHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReadHistory::getUserId, userId)
               .orderByDesc(ReadHistory::getReadTime);
        List<ReadHistory> histories = list(wrapper);
        
        if (histories.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有小说ID
        List<Long> novelIds = histories.stream()
            .map(ReadHistory::getNovelId)
            .collect(Collectors.toList());
        
        // 批量查询小说信息
        LambdaQueryWrapper<Novel> novelWrapper = new LambdaQueryWrapper<>();
        novelWrapper.in(Novel::getNovelId, novelIds);
        List<Novel> novels = novelService.list(novelWrapper);
        Map<Long, Novel> novelMap = novels.stream()
            .collect(Collectors.toMap(Novel::getNovelId, n -> n));
        
        // 获取所有章节ID
        List<Long> chapterIds = histories.stream()
            .map(ReadHistory::getChapterId)
            .collect(Collectors.toList());
        
        // 批量查询章节信息
        LambdaQueryWrapper<Chapter> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.in(Chapter::getChapterId, chapterIds);
        List<Chapter> chapters = chapterService.list(chapterWrapper);
        Map<Long, Chapter> chapterMap = chapters.stream()
            .collect(Collectors.toMap(Chapter::getChapterId, c -> c));
        
        // 获取所有分类
        List<Category> categories = categoryService.list();
        Map<Integer, String> categoryMap = categories.stream()
            .collect(Collectors.toMap(Category::getCategoryId, Category::getName));
        
        // 组装DTO
        return histories.stream()
            .map(history -> {
                Novel novel = novelMap.get(history.getNovelId());
                Chapter chapter = chapterMap.get(history.getChapterId());
                
                if (novel == null) {
                    return null;
                }
                
                ReadHistoryDTO dto = new ReadHistoryDTO();
                dto.setHistoryId(history.getHistoryId());
                dto.setNovelId(novel.getNovelId());
                dto.setNovelTitle(novel.getTitle());
                dto.setNovelCover(novel.getCover());
                dto.setAuthor(novel.getAuthor());
                dto.setCategoryName(categoryMap.get(novel.getCategoryId()));
                dto.setChapterId(history.getChapterId());
                dto.setChapterTitle(chapter != null ? chapter.getTitle() : "未知章节");
                dto.setReadTime(history.getReadTime());
                return dto;
            })
            .filter(dto -> dto != null)
            .collect(Collectors.toList());
    }
}
