package com.novel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.novel.common.Result;
import com.novel.dto.request.NovelQuery;
import com.novel.dto.response.NovelDetail;
import com.novel.dto.response.NovelListItem;
import com.novel.entity.Novel;
import com.novel.service.NovelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/novel")
public class NovelController {
    
    private final NovelService novelService;
    
    public NovelController(NovelService novelService) {
        this.novelService = novelService;
    }
    
    /**
     * 获取小说列表
     */
    @GetMapping("/list")
    public Result<Page<NovelListItem>> getNovelList(NovelQuery query) {
        Page<NovelListItem> page = novelService.pageList(query);
        return Result.success(page);
    }
    
    /**
     * 获取小说详情
     */
    @GetMapping("/{novelId}")
    public Result<NovelDetail> getNovelDetail(@PathVariable Long novelId) {
        NovelDetail detail = novelService.getDetail(novelId);
        return Result.success(detail);
    }
    
    /**
     * 获取推荐小说
     */
    @GetMapping("/{novelId}/recommend")
    public Result<List<Novel>> getRecommend(@PathVariable Long novelId, 
                                            @RequestParam(defaultValue = "5") Integer limit) {
        List<Novel> novels = novelService.getRecommend(novelId, limit);
        return Result.success(novels);
    }
    
    /**
     * 增加点击量
     */
    @PostMapping("/{novelId}/click")
    public Result<Void> incrementClick(@PathVariable Long novelId) {
        novelService.incrementClick(novelId);
        return Result.success(null);
    }
}
