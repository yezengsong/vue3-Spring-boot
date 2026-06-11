package com.novel;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("admin123 -> " + encoder.encode("admin123"));
        System.out.println("user123 -> " + encoder.encode("user123"));
    }
}
