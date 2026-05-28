package com.library.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class OperationLogExportVO {
    @ExcelProperty("模块")
    private String module;

    @ExcelProperty("动作")
    private String action;

    @ExcelProperty("操作人")
    private String username;

    @ExcelProperty("请求方式")
    private String method;

    @ExcelProperty("请求路径")
    private String requestUri;

    @ExcelProperty("IP")
    private String ipAddress;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("时间")
    private String createTime;
}
