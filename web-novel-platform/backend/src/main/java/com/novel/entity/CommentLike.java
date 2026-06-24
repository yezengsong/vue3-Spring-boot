package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论点赞实体类
 */
@Data
@TableName("comment_like")
public class CommentLike {
    
    @TableId(value = "like_id", type = IdType.AUTO)
    private Long likeId;
    
    private Long commentId;
    
    private Long userId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
