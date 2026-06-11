package com.novel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.entity.Category;
import com.novel.mapper.CategoryMapper;
import com.novel.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    
    @Override
    public List<Category> getAllCategories() {
        return list();
    }
    
    @Override
    public Category getById(Integer id) {
        return baseMapper.selectById(id);
    }
}
