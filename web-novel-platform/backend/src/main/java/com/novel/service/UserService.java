package com.novel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.novel.dto.request.LoginRequest;
import com.novel.dto.request.RegisterRequest;
import com.novel.dto.response.LoginResponse;
import com.novel.entity.User;

public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    User register(RegisterRequest request);
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);
    
    /**
     * 根据 ID 查询用户
     */
    User getById(Long id);
    
    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, User user);
}
