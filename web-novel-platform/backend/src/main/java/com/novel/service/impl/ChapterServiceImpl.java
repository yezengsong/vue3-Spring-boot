package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.dto.response.ChapterContent;
import com.novel.entity.Chapter;
import com.novel.mapper.ChapterMapper;
import com.novel.service.ChapterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {
    
    @Override
    public List<Chapter> getChaptersByNovelId(Long novelId) {
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getNovelId, novelId)
               .orderByAsc(Chapter::getOrderNum);
        return list(wrapper);
    }
    
    @Override
    public ChapterContent getChapterContent(Long novelId, Long chapterId) {
        Chapter chapter = getById(chapterId);
        if (chapter == null || !chapter.getNovelId().equals(novelId)) {
            throw new RuntimeException("章节不存在");
        }
        
        ChapterContent content = new ChapterContent();
        content.setId(chapter.getId());
        content.setNovelId(chapter.getNovelId());
        content.setTitle(chapter.getTitle());
        content.setContent(chapter.getContent());
        content.setOrderNum(chapter.getOrderNum());
        content.setWordCount(chapter.getWordCount());
        content.setCreateTime(chapter.getCreateTime());
        
        // 查询上一章
        LambdaQueryWrapper<Chapter> prevWrapper = new LambdaQueryWrapper<>();
        prevWrapper.eq(Chapter::getNovelId, novelId)
                   .lt(Chapter::getOrderNum, chapter.getOrderNum())
                   .orderByDesc(Chapter::getOrderNum)
                   .last("LIMIT 1");
        Chapter prevChapter = getOne(prevWrapper);
        content.setPrevChapterId(prevChapter != null ? prevChapter.getId() : null);
        
        // 查询下一章
        LambdaQueryWrapper<Chapter> nextWrapper = new LambdaQueryWrapper<>();
        nextWrapper.eq(Chapter::getNovelId, novelId)
                   .gt(Chapter::getOrderNum, chapter.getOrderNum())
                   .orderByAsc(Chapter::getOrderNum)
                   .last("LIMIT 1");
        Chapter nextChapter = getOne(nextWrapper);
        content.setNextChapterId(nextChapter != null ? nextChapter.getId() : null);
        
        return content;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Chapter createChapter(Chapter chapter) {
        save(chapter);
        
        // 更新小说字数
        updateNovelWordCount(chapter.getNovelId());
        
        return chapter;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChapter(Chapter chapter) {
        updateById(chapter);
        updateNovelWordCount(chapter.getNovelId());
    }
    
    private void updateNovelWordCount(Long novelId) {
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getNovelId, novelId)
               .select(Chapter::getId);
        Long count = count(wrapper);
        
        // 这里简化处理，实际应该累加所有章节字数
        // update novel word count logic can be added here
    }
}
