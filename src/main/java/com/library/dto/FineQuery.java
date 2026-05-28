package com.library.dto;

import lombok.Data;

@Data
public class FineQuery extends PageQuery {
    private Long readerId;
    private Integer status;
}
