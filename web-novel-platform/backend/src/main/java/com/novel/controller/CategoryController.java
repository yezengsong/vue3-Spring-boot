package com.novel.controller;

import com.novel.common.Result;
import com.novel.entity.Category;
import com.novel.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    /**
     * 获取所有分类
     */
    @GetMapping("/list")
    public Result<List<Category>> getCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }
}
