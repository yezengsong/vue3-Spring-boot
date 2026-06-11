package com.novel.controller;

import com.novel.common.Result;
import com.novel.dto.request.CommentRequest;
import com.novel.dto.response.CommentDTO;
import com.novel.entity.Comment;
import com.novel.service.CommentService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chapter")
public class CommentController {
    
    private final CommentService commentService;
    
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    
    /**
     * 获取章节的评论列表
     */
    @GetMapping("/{chapterId}/comments")
    public Result<List<CommentDTO>> getComments(@PathVariable Long chapterId) {
        List<CommentDTO> comments = commentService.getCommentsByChapterId(chapterId);
        return Result.success(comments);
    }
    
    /**
     * 发表评论
     */
    @PostMapping("/{chapterId}/comments")
    public Result<Comment> createComment(@PathVariable Long chapterId,
                                         @Valid @RequestBody CommentRequest request,
                                         Principal principal) {
        Long userId = getCurrentUserId(principal);
        Comment comment = commentService.createComment(chapterId, userId, request);
        return Result.success(comment);
    }
    
    /**
     * 回复评论
     */
    @PostMapping("/{chapterId}/comments/{commentId}/reply")
    public Result<Comment> replyComment(@PathVariable Long chapterId,
                                        @PathVariable Long commentId,
                                        @Valid @RequestBody CommentRequest request,
                                        Principal principal) {
        Long userId = getCurrentUserId(principal);
        request.setParentId(commentId);
        Comment comment = commentService.replyComment(chapterId, userId, request);
        return Result.success(comment);
    }
    
    /**
     * 点赞评论
     */
    @PostMapping("/comments/{commentId}/like")
    public Result<Void> likeComment(@PathVariable Long commentId) {
        commentService.likeComment(commentId);
        return Result.success();
    }
    
    private Long getCurrentUserId(Principal principal) {
        // TODO: 从 principal 中获取用户 ID
        return 1L;
    }
}
