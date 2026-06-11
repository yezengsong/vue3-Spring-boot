package com.novel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.novel.dto.request.NovelQuery;
import com.novel.dto.response.NovelDetail;
import com.novel.dto.response.NovelListItem;
import com.novel.entity.Novel;

import java.util.List;

public interface NovelService extends IService<Novel> {
    
    /**
     * 分页查询小说列表
     */
    Page<NovelListItem> pageList(NovelQuery query);
    
    /**
     * 获取小说详情
     */
    NovelDetail getDetail(Long novelId);
    
    /**
     * 获取推荐小说
     */
    List<Novel> getRecommend(Long novelId, Integer limit);
    
    /**
     * 增加点击量
     */
    void incrementClick(Long novelId);
    
    /**
     * 后台：新增小说
     */
    Novel createNovel(Novel novel);
    
    /**
     * 后台：更新小说
     */
    void updateNovel(Novel novel);
    
    /**
     * 根据分类名称获取分类 ID
     */
    Long getCategoryIdByName(String categoryName);
    
    /**
     * 根据小说标题获取小说 ID
     */
    Long getNovelIdByTitle(String title);
}
