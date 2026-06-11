package com.novel.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论 DTO
 */
@Data
public class CommentDTO {
    
    private Long id;
    private Long chapterId;
    private Long userId;
    private String username;
    private String avatar;
    private String content;
    private Long parentId;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createTime;
}
