package com.novel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.novel.dto.request.CommentRequest;
import com.novel.dto.response.CommentDTO;
import com.novel.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    
    /**
     * 获取章节的评论列表
     */
    List<CommentDTO> getCommentsByChapterId(Long chapterId);
    
    /**
     * 发表评论
     */
    Comment createComment(Long chapterId, Long userId, CommentRequest request);
    
    /**
     * 回复评论
     */
    Comment replyComment(Long chapterId, Long userId, CommentRequest request);
    
    /**
     * 点赞评论
     */
    void likeComment(Long commentId);
    
    /**
     * 审核评论
     */
    void auditComment(Long commentId, Integer status);
    
    /**
     * 删除评论
     */
    void deleteComment(Long commentId);
    
    /**
     * 按小说 ID 查询评论列表（包含章节信息）
     */
    List<CommentDTO> getCommentsByNovelId(Long novelId);
}
