package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {
    
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    
    private String username;
    
    private String password;
    
    private String email;
    
    private String avatar;
    
    @TableField("role")
    private String role;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private Integer deleted;
}
