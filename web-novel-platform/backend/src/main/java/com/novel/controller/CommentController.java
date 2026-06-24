package com.novel.controller;

import com.novel.common.Result;
import com.novel.dto.request.CommentRequest;
import com.novel.dto.response.CommentDTO;
import com.novel.entity.Comment;
import com.novel.security.JwtUserDetails;
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
    
    /**
     * 获取当前用户的评论列表
     */
    @GetMapping("/my")
    public Result<List<CommentDTO>> getMyComments(Principal principal) {
        Long userId = getCurrentUserId(principal);
        if (userId == null) {
            return Result.error("未登录");
        }
        List<CommentDTO> comments = commentService.getCommentsByUserId(userId);
        return Result.success(comments);
    }
    
    /**
     * 删除自己的评论
     */
    @DeleteMapping("/my/{commentId}")
    public Result<Void> deleteMyComment(@PathVariable Long commentId, Principal principal) {
        Long userId = getCurrentUserId(principal);
        if (userId == null) {
            return Result.error("未登录");
        }
        commentService.deleteComment(commentId);
        return Result.success();
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
        // 如果无法从 Principal 中获取用户 ID，返回 null
        // 调用方应该处理这种情况（例如返回 401 Unauthorized）
        return null;
    }
}
