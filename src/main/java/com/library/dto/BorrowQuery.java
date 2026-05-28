package com.library.dto;

import lombok.Data;

@Data
public class BorrowQuery extends PageQuery {
    private Long readerId;
    private Long bookId;
    private Integer status;
    private Boolean overdueOnly;
}
