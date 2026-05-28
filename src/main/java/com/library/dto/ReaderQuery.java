package com.library.dto;

import lombok.Data;

@Data
public class ReaderQuery extends PageQuery {
    private String keyword;
    private Integer status;
}
