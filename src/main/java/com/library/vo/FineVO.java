package com.library.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FineVO {
    private Long id;
    private Long borrowRecordId;
    private Long readerId;
    private String readerNo;
    private String readerName;
    private Long bookId;
    private String bookCode;
    private String bookTitle;
    private String author;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BigDecimal amount;
    private String reason;
    private Integer status;
    private String payType;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
}
