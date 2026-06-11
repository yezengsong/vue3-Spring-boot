package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 章节实体类
 */
@Data
@TableName("chapter")
public class Chapter {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long novelId;
    
    private String title;
    
    private String content;
    
    private Integer orderNum;
    
    private Integer wordCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 小说名称（仅用于接收前端传来的小说名称，不映射到数据库）
     */
    @TableField(exist = false)
    private String novelName;
}
