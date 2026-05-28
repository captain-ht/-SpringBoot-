package com.library.dto;

import lombok.Data;

@Data
public class PurchaseQuery extends PageQuery {
    private Integer status;
    private String keyword;
}
