package com.library.modules.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.result.PageResult;
import com.library.dto.LogQuery;
import com.library.modules.log.entity.OperationLogRecord;
import jakarta.servlet.http.HttpServletResponse;

public interface OperationLogService extends IService<OperationLogRecord> {
    PageResult<OperationLogRecord> pageLogs(LogQuery query);
    void exportLogs(HttpServletResponse response);
}
