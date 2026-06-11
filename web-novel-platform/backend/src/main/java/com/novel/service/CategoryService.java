package com.novel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.novel.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    
    /**
     * 获取所有分类
     */
    List<Category> getAllCategories();
    
    /**
     * 根据 ID 获取分类
     */
    Category getById(Integer id);
}
