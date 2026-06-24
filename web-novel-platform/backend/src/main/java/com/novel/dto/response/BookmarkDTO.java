package com.novel.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 书架书籍DTO
 */
@Data
public class BookmarkDTO {
    
    private Long bookmarkId;
    
    private Long novelId;
    
    private String title;
    
    private String author;
    
    private String cover;
    
    private String categoryName;
    
    private String description;
    
    private Integer status;
    
    private Long wordCount;
    
    private LocalDateTime createTime;
}
