package com.library.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ReaderExportVO {
    @ExcelProperty("读者编号")
    private String readerNo;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("读者类型")
    private Integer readerType;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("部门")
    private String department;

    @ExcelProperty("最大借阅数")
    private Integer maxBorrowCount;

    @ExcelProperty("借阅天数")
    private Integer borrowDays;
}
