package com.novel.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小说列表项 DTO
 */
@Data
public class NovelListItem {
    
    private Long id;
    private String title;
    private String author;
    private String cover;
    private String categoryName;
    private String description;
    private Integer status;
    private Long wordCount;
    private Long clickCount;
    private LocalDateTime createTime;
}
