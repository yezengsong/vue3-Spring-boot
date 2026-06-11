package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
@TableName("comment")
public class Comment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long chapterId;
    
    private Long userId;
    
    private String content;
    
    private Long parentId;
    
    private Integer likeCount;
    
    private Integer status;  // 0:待审核 1:已通过 2:已拒绝
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
