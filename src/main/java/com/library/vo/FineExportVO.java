package com.library.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class FineExportVO {
    @ExcelProperty("读者姓名")
    private String readerName;

    @ExcelProperty("读者编号")
    private String readerNo;

    @ExcelProperty("图书名称")
    private String bookTitle;

    @ExcelProperty("图书编码")
    private String bookCode;

    @ExcelProperty("金额")
    private String amount;

    @ExcelProperty("原因")
    private String reason;

    @ExcelProperty("支付状态")
    private String statusText;

    @ExcelProperty("支付方式")
    private String payType;

    @ExcelProperty("支付时间")
    private String payTime;
}
