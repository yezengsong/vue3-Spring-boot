package com.novel.dto.request;

import lombok.Data;

/**
 * 小说列表查询参数
 */
@Data
public class NovelQuery {
    
    private Integer page = 1;
    private Integer size = 20;
    private Long categoryId;
    private Integer status;
    private String keyword;
    private String sortBy;  // click_count, bookmark_count, create_time
}
