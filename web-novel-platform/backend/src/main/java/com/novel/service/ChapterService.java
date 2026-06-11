package com.novel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.novel.dto.response.ChapterContent;
import com.novel.entity.Chapter;

import java.util.List;

public interface ChapterService extends IService<Chapter> {
    
    /**
     * 获取小说的章节列表
     */
    List<Chapter> getChaptersByNovelId(Long novelId);
    
    /**
     * 获取章节内容（包含上一章和下一章信息）
     */
    ChapterContent getChapterContent(Long novelId, Long chapterId);
    
    /**
     * 后台：新增章节
     */
    Chapter createChapter(Chapter chapter);
    
    /**
     * 后台：更新章节
     */
    void updateChapter(Chapter chapter);
}
