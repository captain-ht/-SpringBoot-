package com.library.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BorrowRecordVO {
    private Long id;
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
    private Integer overdueDays;
    private Integer renewCount;
    private Integer status;
    private Boolean overdue;
    private LocalDateTime createTime;
}
