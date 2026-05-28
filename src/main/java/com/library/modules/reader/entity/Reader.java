package com.library.modules.reader.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@TableName("library_reader")
@EqualsAndHashCode(callSuper = true)
public class Reader extends BaseEntity {
    @TableId
    private Long id;
    private String readerNo;
    private String name;
    private Integer readerType;
    private String gender;
    private String phone;
    private String email;
    private String department;
    private LocalDate registerDate;
    private Integer maxBorrowCount;
    private Integer borrowDays;
    private Integer status;
}
