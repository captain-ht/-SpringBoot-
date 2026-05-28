package com.library.dto;

import lombok.Data;

@Data
public class BookQuery extends PageQuery {
    private String keyword;
    private Long categoryId;
    private Integer status;
}
