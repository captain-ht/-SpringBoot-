package com.library.modules.log.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.result.PageResult;
import com.library.dto.LogQuery;
import com.library.modules.log.entity.OperationLogRecord;
import com.library.modules.log.mapper.OperationLogRecordMapper;
import com.library.modules.log.service.OperationLogService;
import com.library.vo.OperationLogExportVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogRecordMapper, OperationLogRecord> implements OperationLogService {
    @Override
    public PageResult<OperationLogRecord> pageLogs(LogQuery query) {
        LambdaQueryWrapper<OperationLogRecord> wrapper = new LambdaQueryWrapper<OperationLogRecord>()
                .like(StringUtils.hasText(query.getModule()), OperationLogRecord::getModule, query.getModule())
                .like(StringUtils.hasText(query.getAction()), OperationLogRecord::getAction, query.getAction())
                .like(StringUtils.hasText(query.getUsername()), OperationLogRecord::getUsername, query.getUsername())
                .eq(StringUtils.hasText(query.getStatus()), OperationLogRecord::getStatus, query.getStatus())
                .orderByDesc(OperationLogRecord::getId);
        if (StringUtils.hasText(query.getStartTime()) && StringUtils.hasText(query.getEndTime())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            wrapper.between(OperationLogRecord::getCreateTime,
                    LocalDateTime.parse(query.getStartTime(), formatter),
                    LocalDateTime.parse(query.getEndTime(), formatter));
        }
        return PageResult.of(page(new Page<>(query.getCurrent(), query.getSize()), wrapper));
    }

    @Override
    public void exportLogs(HttpServletResponse response) {
        try {
            java.util.List<OperationLogExportVO> rows = list().stream().map(item -> {
                OperationLogExportVO vo = new OperationLogExportVO();
                vo.setModule(item.getModule());
                vo.setAction(item.getAction());
                vo.setUsername(item.getUsername());
                vo.setMethod(item.getMethod());
                vo.setRequestUri(item.getRequestUri());
                vo.setIpAddress(item.getIpAddress());
                vo.setStatus(item.getStatus());
                vo.setCreateTime(String.valueOf(item.getCreateTime()));
                return vo;
            }).collect(Collectors.toList());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String fileName = URLEncoder.encode("操作日志", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), OperationLogExportVO.class).sheet("日志").doWrite(rows);
        } catch (Exception ex) {
            throw new RuntimeException("导出操作日志失败：" + ex.getMessage(), ex);
        }
    }
}
