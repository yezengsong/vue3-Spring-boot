package com.novel.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论 DTO
 */
@Data
public class CommentDTO {
    
    private Long commentId;
    private Long chapterId;
    private Long userId;
    private String username;
    private String avatar;
    private String content;
    private Long parentId;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createTime;
    
    /**
     * 章节标题
     */
    private String chapterTitle;
    
    /**
     * 小说 ID
     */
    private Long novelId;
    
    /**
     * 小说标题
     */
    private String novelTitle;
    
    /**
     * 回复的用户名（如果是回复评论）
     */
    private String replyToUsername;
    
    /**
     * 回复列表（仅一级评论包含）
     */
    private List<CommentDTO> replies;
}
