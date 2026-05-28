package com.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReturnRequest {
    @NotNull(message = "借阅记录不能为空")
    private Long borrowRecordId;
}
