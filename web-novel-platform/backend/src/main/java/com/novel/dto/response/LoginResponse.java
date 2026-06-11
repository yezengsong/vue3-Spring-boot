package com.novel.dto.response;

import lombok.Data;

/**
 * 用户登录响应 DTO
 */
@Data
public class LoginResponse {
    
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String role;
    private String token;
}
