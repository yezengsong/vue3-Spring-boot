package com.novel.controller;

import com.novel.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {
    
    @Value("${file.upload-path:./uploads}")
    private String uploadPath;
    
    /**
     * 上传图片
     */
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("请上传图片文件");
        }
        
        // 验证文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("图片大小不能超过 5MB");
        }
        
        try {
            // 上传到 src/main/resources/image 目录
            Path basePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "image").normalize();
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString().replace("-", "") + extension;
            
            // 保存文件
            Path dest = basePath.resolve(filename);
            Files.copy(file.getInputStream(), dest);
            
            // 返回访问路径
            return Result.success("/image/" + filename);
        } catch (IOException e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}
