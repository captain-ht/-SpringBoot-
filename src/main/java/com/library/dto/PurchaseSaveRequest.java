package com.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseSaveRequest {
    private Long id;

    @NotBlank(message = "采购单号不能为空")
    private String purchaseNo;

    @NotBlank(message = "图书名称不能为空")
    private String bookTitle;

    @NotBlank(message = "作者不能为空")
    private String author;

    private String isbn;
    private String publisher;

    @NotNull(message = "采购数量不能为空")
    @Min(value = 1, message = "采购数量必须大于 0")
    private Integer quantity;

    private BigDecimal unitPrice;
    private LocalDate expectedDate;
    private String supplier;
    private Integer status;
    private String remark;
}
