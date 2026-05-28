package com.library.modules.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.dto.CategorySaveRequest;
import com.library.modules.category.entity.Category;
import com.library.modules.category.mapper.CategoryMapper;
import com.library.modules.category.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public void saveCategory(CategorySaveRequest request) {
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        saveOrUpdate(category);
    }

    @Override
    public List<Category> listAll() {
        return list(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder, Category::getId));
    }
}
