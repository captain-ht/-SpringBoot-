package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FinePayRequest {
    @NotNull(message = "罚款记录不能为空")
    private Long fineId;

    @NotBlank(message = "支付方式不能为空")
    private String payType;
}
