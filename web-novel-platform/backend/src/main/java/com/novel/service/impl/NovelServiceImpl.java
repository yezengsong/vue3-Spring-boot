package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.dto.request.NovelQuery;
import com.novel.dto.response.NovelDetail;
import com.novel.dto.response.NovelListItem;
import com.novel.entity.Category;
import com.novel.entity.Chapter;
import com.novel.entity.Novel;
import com.novel.mapper.NovelMapper;
import com.novel.service.CategoryService;
import com.novel.service.ChapterService;
import com.novel.service.NovelService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NovelServiceImpl extends ServiceImpl<NovelMapper, Novel> implements NovelService {
    
    private final CategoryService categoryService;
    private final ChapterService chapterService;
    
    public NovelServiceImpl(CategoryService categoryService, ChapterService chapterService) {
        this.categoryService = categoryService;
        this.chapterService = chapterService;
    }
    
    @Override
    public Page<NovelListItem> pageList(NovelQuery query) {
        Page<Novel> page = new Page<>(query.getPage(), query.getSize());
        
        LambdaQueryWrapper<Novel> wrapper = new LambdaQueryWrapper<>();
        // 只显示上架的小说（status=1 连载中 或 status=2 完结）
        wrapper.in(Novel::getStatus, 1, 2);
        
        if (query.getCategoryId() != null) {
            wrapper.eq(Novel::getCategoryId, query.getCategoryId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Novel::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(Novel::getTitle, query.getKeyword())
                           .or()
                           .like(Novel::getAuthor, query.getKeyword()));
        }
        
        // 排序
        String sortBy = query.getSortBy();
        if ("click_count".equals(sortBy)) {
            wrapper.orderByDesc(Novel::getClickCount);
        } else if ("bookmark_count".equals(sortBy)) {
            wrapper.orderByDesc(Novel::getBookmarkCount);
        } else {
            wrapper.orderByDesc(Novel::getCreateTime);
        }
        
        Page<Novel> novelPage = page(page, wrapper);
        
        // 转换为 DTO
        Page<NovelListItem> result = new Page<>();
        BeanUtils.copyProperties(novelPage, result, "records");
        result.setRecords(novelPage.getRecords().stream()
            .map(this::convertToListItem)
            .collect(Collectors.toList()));
        
        return result;
    }
    
    @Override
    public NovelDetail getDetail(Long novelId) {
        Novel novel = getById(novelId);
        if (novel == null) {
            throw new RuntimeException("小说不存在");
        }
        
        NovelDetail detail = new NovelDetail();
        BeanUtils.copyProperties(novel, detail);
        
        // 获取分类名称
        Category category = categoryService.getById(novel.getCategoryId().intValue());
        if (category != null) {
            detail.setCategoryName(category.getName());
        }
        
        // 获取章节列表（简化版）
        List<Chapter> chapters = chapterService.getChaptersByNovelId(novelId);
        detail.setChapters(chapters.stream()
            .map(chapter -> {
                NovelDetail.ChapterSimple simple = new NovelDetail.ChapterSimple();
                simple.setId(chapter.getId());
                simple.setTitle(chapter.getTitle());
                simple.setOrderNum(chapter.getOrderNum());
                return simple;
            })
            .collect(Collectors.toList()));
        
        return detail;
    }
    
    @Override
    public List<Novel> getRecommend(Long novelId, Integer limit) {
        Novel novel = getById(novelId);
        if (novel == null) {
            return list();
        }
        
        LambdaQueryWrapper<Novel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Novel::getCategoryId, novel.getCategoryId())
               .ne(Novel::getId, novelId)
               .eq(Novel::getStatus, 1)
               .orderByDesc(Novel::getClickCount)
               .last("LIMIT " + limit);
        
        return list(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementClick(Long novelId) {
        baseMapper.update(null, 
            new LambdaUpdateWrapper<Novel>()
                .setSql("click_count = click_count + 1")
                .eq(Novel::getId, novelId));
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Novel createNovel(Novel novel) {
        novel.setBookmarkCount(0);
        novel.setClickCount(0L);
        novel.setWordCount(0L);
        save(novel);
        return novel;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNovel(Novel novel) {
        updateById(novel);
    }
    
    @Override
    public Long getCategoryIdByName(String categoryName) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, categoryName);
        Category category = categoryService.getOne(wrapper);
        return category != null ? category.getId().longValue() : null;
    }
    
    @Override
    public Long getNovelIdByTitle(String title) {
        LambdaQueryWrapper<Novel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Novel::getTitle, title);
        Novel novel = getOne(wrapper);
        return novel != null ? novel.getId() : null;
    }
    
    private NovelListItem convertToListItem(Novel novel) {
        NovelListItem item = new NovelListItem();
        BeanUtils.copyProperties(novel, item);
        
        Category category = categoryService.getById(novel.getCategoryId().intValue());
        if (category != null) {
            item.setCategoryName(category.getName());
        }
        
        return item;
    }
}
