package com.novel.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 小说详情 DTO
 */
@Data
public class NovelDetail {
    
    private Long novelId;
    private String title;
    private String author;
    private String cover;
    private Long categoryId;
    private String categoryName;
    private String description;
    private Integer status;
    private Long wordCount;
    private Long clickCount;
    private Integer bookmarkCount;
    private LocalDateTime createTime;
    private List<ChapterSimple> chapters;
    
    @Data
    public static class ChapterSimple {
        private Long chapterId;
        private String title;
        private Integer orderNum;
    }
}
