package com.novel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-path:./uploads}")
    private String uploadPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 上传文件访问路径 - 使用绝对路径
        String imageDir = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "image").normalize().toUri().toString();
        registry.addResourceHandler("/image/**")
                .addResourceLocations(imageDir.endsWith("/") ? imageDir : imageDir + "/");
        
        // 旧上传路径兼容
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
