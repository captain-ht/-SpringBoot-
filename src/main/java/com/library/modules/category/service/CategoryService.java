package com.library.modules.category.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.dto.CategorySaveRequest;
import com.library.modules.category.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    void saveCategory(CategorySaveRequest request);
    List<Category> listAll();
}
