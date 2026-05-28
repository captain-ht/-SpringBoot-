package com.library.modules.book.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("library_book")
@EqualsAndHashCode(callSuper = true)
public class Book extends BaseEntity {
    @TableId
    private Long id;
    private String bookCode;
    private String isbn;
    private String title;
    private String author;
    private Long categoryId;
    private String publisher;
    private LocalDate publishDate;
    private BigDecimal price;
    private Integer totalStock;
    private Integer availableStock;
    private Integer borrowedCount;
    private Integer status;
    private String location;
    private String coverUrl;
    private String description;
}
