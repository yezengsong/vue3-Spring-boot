package com.novel.common;

import lombok.Getter;

/**
 * 返回状态码枚举
 */
@Getter
public enum ResultCode {
    
    SUCCESS(200, "成功"),
    ERROR(500, "失败"),
    
    // 用户相关 1000-1999
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    USER_DISABLED(1004, "用户已被禁用"),
    TOKEN_INVALID(1005, "Token 无效或已过期"),
    
    // 小说相关 2000-2999
    NOVEL_NOT_FOUND(2001, "小说不存在"),
    CHAPTER_NOT_FOUND(2002, "章节不存在"),
    
    // 评论相关 3000-3999
    COMMENT_NOT_FOUND(3001, "评论不存在"),
    COMMENT_AUDIT_PENDING(3002, "评论待审核"),
    
    // 权限相关 4000-4999
    UNAUTHORIZED(4001, "未授权"),
    FORBIDDEN(4003, "无权限");
    
    private final Integer code;
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
