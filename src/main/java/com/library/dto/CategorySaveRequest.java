package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategorySaveRequest {
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String code;
    private String description;
    private Integer sortOrder;
}
