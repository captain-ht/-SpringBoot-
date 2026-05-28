package com.library.dto;

import lombok.Data;

@Data
public class ReservationQuery extends PageQuery {
    private Long readerId;
    private Long bookId;
    private Integer status;
}
