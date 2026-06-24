package com.novel.controller;

import com.novel.common.Result;
import com.novel.dto.response.ChapterContent;
import com.novel.entity.Chapter;
import com.novel.security.JwtUserDetails;
import com.novel.service.ChapterService;
import com.novel.service.ReadHistoryService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/novel/{novelId}")
public class ChapterController {
    
    private final ChapterService chapterService;
    
    private final ReadHistoryService readHistoryService;
    
    public ChapterController(ChapterService chapterService, ReadHistoryService readHistoryService) {
        this.chapterService = chapterService;
        this.readHistoryService = readHistoryService;
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
                                                     @PathVariable Long chapterId,
                                                     Principal principal) {
        ChapterContent content = chapterService.getChapterContent(novelId, chapterId);
        
        // 记录阅读历史
        Long userId = getCurrentUserId(principal);
        if (userId != null) {
            readHistoryService.recordReadHistory(userId, novelId, chapterId);
        }
        
        return Result.success(content);
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
        return null;
    }
}
