package com.novel.controller;

import com.novel.common.Result;
import com.novel.dto.request.LoginRequest;
import com.novel.dto.request.RegisterRequest;
import com.novel.dto.response.LoginResponse;
import com.novel.entity.User;
import com.novel.security.JwtUserDetails;
import com.novel.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails) {
            JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
            User user = userService.getById(userDetails.getId());
            return Result.success(user);
        }
        return Result.error("未登录");
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails) {
            JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
            userService.updateUserInfo(userDetails.getId(), user);
            return Result.success();
        }
        return Result.error("未登录");
    }
}
