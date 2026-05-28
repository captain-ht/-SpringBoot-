package com.library.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BookExportVO {
    @ExcelProperty("图书编码")
    private String bookCode;

    @ExcelProperty("ISBN")
    private String isbn;

    @ExcelProperty("图书名称")
    private String title;

    @ExcelProperty("作者")
    private String author;

    @ExcelProperty("出版社")
    private String publisher;

    @ExcelProperty("总库存")
    private Integer totalStock;

    @ExcelProperty("可借库存")
    private Integer availableStock;

    @ExcelProperty("借出次数")
    private Integer borrowedCount;

    @ExcelProperty("馆藏位置")
    private String location;
}
