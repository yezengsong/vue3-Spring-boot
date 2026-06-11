package com.novel.controller.admin;

import com.novel.common.Result;
import com.novel.dto.request.CommentRequest;
import com.novel.dto.response.CommentDTO;
import com.novel.entity.Chapter;
import com.novel.entity.Comment;
import com.novel.entity.Novel;
import com.novel.service.ChapterService;
import com.novel.service.CommentService;
import com.novel.service.NovelService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    private final NovelService novelService;
    private final ChapterService chapterService;
    private final CommentService commentService;
    
    public AdminController(NovelService novelService, 
                          ChapterService chapterService,
                          CommentService commentService) {
        this.novelService = novelService;
        this.chapterService = chapterService;
        this.commentService = commentService;
    }
    
    // ========== 小说管理 ==========
    
    /**
     * 新增小说
     */
    @PostMapping("/novel")
    public Result<Novel> createNovel(@RequestBody Novel novel) {
        // 如果提供了分类名称而不是分类 ID，则查找分类 ID
        if (novel.getCategoryId() == null && novel.getCategoryName() != null) {
            Long categoryId = novelService.getCategoryIdByName(novel.getCategoryName());
            if (categoryId == null) {
                return Result.error("分类名称不存在：" + novel.getCategoryName());
            }
            novel.setCategoryId(categoryId);
        }
        Novel created = novelService.createNovel(novel);
        return Result.success(created);
    }
    
    /**
     * 更新小说
     */
    @PutMapping("/novel/{id}")
    public Result<Void> updateNovel(@PathVariable Long id, @RequestBody Novel novel) {
        // 如果提供了分类名称而不是分类 ID，则查找分类 ID
        if (novel.getCategoryId() == null && novel.getCategoryName() != null) {
            Long categoryId = novelService.getCategoryIdByName(novel.getCategoryName());
            if (categoryId == null) {
                return Result.error("分类名称不存在：" + novel.getCategoryName());
            }
            novel.setCategoryId(categoryId);
        }
        novel.setId(id);
        novelService.updateNovel(novel);
        return Result.success();
    }
    
    /**
     * 删除小说
     */
    @DeleteMapping("/novel/{id}")
    public Result<Void> deleteNovel(@PathVariable Long id) {
        novelService.removeById(id);
        return Result.success();
    }
    
    // ========== 章节管理 ==========
    
    /**
     * 新增章节（支持通过小说名称或小说 ID）
     */
    @PostMapping("/novel/chapter")
    public Result<Chapter> createChapter(@RequestParam(required = false) Long novelId,
                                         @RequestParam(required = false) String novelName,
                                         @RequestBody Chapter chapter) {
        // 如果提供了小说名称而不是小说 ID，则查找小说 ID
        if (novelId == null && novelName != null) {
            novelId = novelService.getNovelIdByTitle(novelName);
            if (novelId == null) {
                return Result.error("小说名称不存在：" + novelName);
            }
        }
        if (novelId == null) {
            return Result.error("必须提供 novelId 或 novelName 参数");
        }
        chapter.setNovelId(novelId);
        Chapter created = chapterService.createChapter(chapter);
        return Result.success(created);
    }
    
    /**
     * 更新章节
     */
    @PutMapping("/chapter/{id}")
    public Result<Void> updateChapter(@PathVariable Long id, 
                                      @RequestBody Chapter chapter) {
        chapter.setId(id);
        chapterService.updateChapter(chapter);
        return Result.success();
    }
    
    /**
     * 删除章节
     */
    @DeleteMapping("/chapter/{id}")
    public Result<Void> deleteChapter(@PathVariable Long id) {
        chapterService.removeById(id);
        return Result.success();
    }
    
    // ========== 评论管理 ==========
    
    /**
     * 获取小说的评论列表
     */
    @GetMapping("/novel/{novelId}/comments")
    public Result<List<CommentDTO>> getCommentsByNovelId(@PathVariable Long novelId) {
        List<CommentDTO> comments = commentService.getCommentsByNovelId(novelId);
        return Result.success(comments);
    }
    
    /**
     * 审核评论
     */
    @PutMapping("/comment/{id}/audit")
    public Result<Void> auditComment(@PathVariable Long id,
                                     @RequestParam Integer status) {
        commentService.auditComment(id, status);
        return Result.success();
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/comment/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success();
    }
}
