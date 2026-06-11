package com.novel.controller;

import com.novel.common.Result;
import com.novel.dto.response.ChapterContent;
import com.novel.entity.Chapter;
import com.novel.service.ChapterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/novel/{novelId}")
public class ChapterController {
    
    private final ChapterService chapterService;
    
    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }
    
    /**
     * 获取小说的章节列表
     */
    @GetMapping("/chapters")
    public Result<List<Chapter>> getChapters(@PathVariable Long novelId) {
        List<Chapter> chapters = chapterService.getChaptersByNovelId(novelId);
        return Result.success(chapters);
    }
    
    /**
     * 获取章节内容
     */
    @GetMapping("/chapter/{chapterId}")
    public Result<ChapterContent> getChapterContent(@PathVariable Long novelId,
                                                     @PathVariable Long chapterId) {
        ChapterContent content = chapterService.getChapterContent(novelId, chapterId);
        return Result.success(content);
    }
}
