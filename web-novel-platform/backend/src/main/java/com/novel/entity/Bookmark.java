package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏实体类
 */
@Data
@TableName("bookmark")
public class Bookmark {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long novelId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
