package com.novel;

import cn.hutool.crypto.digest.BCrypt;

public class PasswordTest {
    public static void main(String[] args) {
        String adminHash = BCrypt.hashpw("admin123");
        String userHash = BCrypt.hashpw("user123");
        
        System.out.println("admin123 -> " + adminHash);
        System.out.println("user123 -> " + userHash);
        
        // 验证
        System.out.println("\nVerify admin: " + BCrypt.checkpw("admin123", adminHash));
        System.out.println("Verify user: " + BCrypt.checkpw("user123", userHash));
    }
}
