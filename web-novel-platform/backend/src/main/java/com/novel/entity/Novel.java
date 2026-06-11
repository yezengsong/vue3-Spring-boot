package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小说实体类
 */
@Data
@TableName("novel")
public class Novel {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String author;
    
    private String cover;
    
    private Long categoryId;
    
    private String description;
    
    private Integer status;  // 0:下架 1:连载 2:完结
    
    private Long wordCount;
    
    private Long clickCount;
    
    private Integer bookmarkCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 分类名称（仅用于接收前端传来的分类名称，不映射到数据库）
     */
    @TableField(exist = false)
    private String categoryName;
}
