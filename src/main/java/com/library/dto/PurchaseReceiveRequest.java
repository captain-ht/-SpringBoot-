package com.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseReceiveRequest {
    @NotNull(message = "采购单不能为空")
    private Long purchaseId;

    @NotNull(message = "图书分类不能为空")
    private Long categoryId;

    private String location;
    private String description;
    private String coverUrl;
}
