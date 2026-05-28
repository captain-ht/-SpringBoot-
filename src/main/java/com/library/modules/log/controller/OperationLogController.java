package com.library.modules.log.controller;

import com.library.common.result.ApiResult;
import com.library.common.result.PageResult;
import com.library.dto.LogQuery;
import com.library.modules.log.entity.OperationLogRecord;
import com.library.modules.log.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Operation(summary = "操作日志分页查询")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<PageResult<OperationLogRecord>> page(LogQuery query) {
        return ApiResult.success(operationLogService.pageLogs(query));
    }

    @Operation(summary = "导出操作日志")
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public void export(HttpServletResponse response) {
        operationLogService.exportLogs(response);
    }
}
