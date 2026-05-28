package com.library.modules.borrow.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("library_borrow_record")
@EqualsAndHashCode(callSuper = true)
public class BorrowRecord extends BaseEntity {
    @TableId
    private Long id;
    private Long readerId;
    private Long bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Integer overdueDays;
    private Integer renewCount;
    private Integer status;
    private LocalDateTime remindTime;
}
