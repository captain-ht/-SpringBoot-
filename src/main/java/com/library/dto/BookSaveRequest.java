package com.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookSaveRequest {
    private Long id;

    @NotBlank(message = "图书编码不能为空")
    private String bookCode;

    @NotBlank(message = "ISBN 不能为空")
    private String isbn;

    @NotBlank(message = "图书名称不能为空")
    private String title;

    @NotBlank(message = "作者不能为空")
    private String author;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private String publisher;
    private LocalDate publishDate;
    private String location;
    private String coverUrl;
    private String description;
    private BigDecimal price;

    @NotNull(message = "馆藏总数不能为空")
    @Min(value = 0, message = "馆藏总数不能小于 0")
    private Integer totalStock;
}
