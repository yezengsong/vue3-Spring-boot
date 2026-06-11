package com.novel.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 章节内容 DTO
 */
@Data
public class ChapterContent {
    
    private Long id;
    private Long novelId;
    private String title;
    private String content;
    private Integer orderNum;
    private Integer wordCount;
    private LocalDateTime createTime;
    
    // 上一章 ID
    private Long prevChapterId;
    // 下一章 ID
    private Long nextChapterId;
}
