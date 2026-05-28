package com.library.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservationVO {
    private Long id;
    private Long readerId;
    private String readerNo;
    private String readerName;
    private Long bookId;
    private String bookCode;
    private String bookTitle;
    private String author;
    private LocalDate reservationDate;
    private LocalDate expireDate;
    private Integer status;
    private LocalDateTime createTime;
}
