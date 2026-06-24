package com.novel.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 阅读历史DTO
 */
@Data
public class ReadHistoryDTO {
    
    private Long historyId;
    
    private Long novelId;
    
    private String novelTitle;
    
    private String novelCover;
    
    private String author;
    
    private String categoryName;
    
    private Long chapterId;
    
    private String chapterTitle;
    
    private LocalDateTime readTime;
}
