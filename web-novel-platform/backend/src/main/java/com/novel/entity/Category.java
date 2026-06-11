package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类实体类
 */
@Data
@TableName("category")
public class Category {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String name;
    
    private Integer sort;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
