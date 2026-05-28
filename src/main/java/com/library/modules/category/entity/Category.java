package com.library.modules.category.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("library_category")
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {
    @TableId
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer sortOrder;
}
