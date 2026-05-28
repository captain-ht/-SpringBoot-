package com.library.modules.category.controller;

import com.library.common.result.ApiResult;
import com.library.dto.CategorySaveRequest;
import com.library.modules.category.entity.Category;
import com.library.modules.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "分类列表")
    @GetMapping
    public ApiResult<List<Category>> list() {
        return ApiResult.success(categoryService.listAll());
    }

    @Operation(summary = "保存分类")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResult<Void> save(@Valid @RequestBody CategorySaveRequest request) {
        categoryService.saveCategory(request);
        return ApiResult.success("保存成功");
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Void> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return ApiResult.success("删除成功");
    }
}
