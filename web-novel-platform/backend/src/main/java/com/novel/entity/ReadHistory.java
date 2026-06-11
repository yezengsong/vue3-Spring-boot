package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 阅读历史实体类
 */
@Data
@TableName("read_history")
public class ReadHistory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long novelId;
    
    private Long chapterId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime readTime;
}
