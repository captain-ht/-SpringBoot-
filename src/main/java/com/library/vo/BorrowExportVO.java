package com.library.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BorrowExportVO {
    @ExcelProperty("读者姓名")
    private String readerName;

    @ExcelProperty("读者编号")
    private String readerNo;

    @ExcelProperty("图书名称")
    private String bookTitle;

    @ExcelProperty("图书编码")
    private String bookCode;

    @ExcelProperty("作者")
    private String author;

    @ExcelProperty("借阅日期")
    private String borrowDate;

    @ExcelProperty("应还日期")
    private String dueDate;

    @ExcelProperty("归还日期")
    private String returnDate;

    @ExcelProperty("逾期天数")
    private Integer overdueDays;

    @ExcelProperty("续借次数")
    private Integer renewCount;
}
