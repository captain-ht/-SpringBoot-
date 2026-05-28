package com.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowRequest {
    @NotNull(message = "读者不能为空")
    private Long readerId;

    @NotNull(message = "图书不能为空")
    private Long bookId;
}
