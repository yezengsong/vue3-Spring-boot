package com.novel.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.common.ResultCode;
import com.novel.dto.request.LoginRequest;
import com.novel.dto.request.RegisterRequest;
import com.novel.dto.response.LoginResponse;
import com.novel.entity.User;
import com.novel.mapper.UserMapper;
import com.novel.security.JwtTokenProvider;
import com.novel.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    public UserServiceImpl(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    public User register(RegisterRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (count(wrapper) > 0) {
            throw new RuntimeException("用户已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setAvatar("/image/default_person.png");
        user.setRole("USER");
        user.setStatus(1);
        
        save(user);
        return user;
    }
    
    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        User user = getByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }
        
        // 生成 Token
        String token = jwtTokenProvider.createToken(user.getUserId(), user.getUsername(), user.getRole());
        
        // 返回登录信息
        LoginResponse response = new LoginResponse();
        BeanUtils.copyProperties(user, response);
        response.setToken(token);
        
        return response;
    }
    
    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }
    
    @Override
    public User getById(Long id) {
        return baseMapper.selectById(id);
    }
    
    @Override
    public void updateUserInfo(Long userId, User user) {
        User existingUser = getById(userId);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 只允许更新部分字段
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getAvatar() != null) {
            existingUser.setAvatar(user.getAvatar());
        }
        
        updateById(existingUser);
    }
    
    @Override
    public IPage<User> getUserList(int page, int size, String username) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.trim().isEmpty()) {
            wrapper.like(User::getUsername, username);
        }
        // 先按角色排序（SUPER_ADMIN > ADMIN > USER），再按创建时间降序
        wrapper.last("ORDER BY FIELD(role, 'SUPER_ADMIN', 'ADMIN', 'USER'), create_time DESC");
        return page(pageParam, wrapper);
    }
    
    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        updateById(user);
    }
    
    @Override
    public void addAdmin(String username, String password, String email) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (count(wrapper) > 0) {
            throw new RuntimeException("用户已存在");
        }
        
        // 创建管理员账号
        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password));
        user.setEmail(email);
        user.setAvatar("/image/default_person.png");
        user.setRole("ADMIN");
        user.setStatus(1);
        
        save(user);
    }
}
