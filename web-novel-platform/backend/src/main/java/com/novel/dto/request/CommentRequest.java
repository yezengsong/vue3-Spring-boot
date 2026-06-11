package com.novel.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 发表评论请求 DTO
 */
@Data
public class CommentRequest {
    
    @NotBlank(message = "评论内容不能为空")
    private String content;
    
    private Long parentId;
}
