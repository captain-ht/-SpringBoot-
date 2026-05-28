package com.library.common.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.common.annotation.OperationLog;
import com.library.common.utils.SecurityUtils;
import com.library.modules.log.entity.OperationLogRecord;
import com.library.modules.log.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    public OperationLogAspect(OperationLogService operationLogService, ObjectMapper objectMapper) {
        this.operationLogService = operationLogService;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        OperationLogRecord logRecord = new OperationLogRecord();
        logRecord.setModule(operationLog.module());
        logRecord.setAction(operationLog.action());
        logRecord.setUsername(SecurityUtils.getCurrentUsername());
        fillRequestInfo(logRecord, joinPoint.getArgs());
        try {
            Object result = joinPoint.proceed();
            logRecord.setStatus("SUCCESS");
            operationLogService.save(logRecord);
            return result;
        } catch (Throwable ex) {
            logRecord.setStatus("FAILED");
            logRecord.setErrorMessage(ex.getMessage());
            operationLogService.save(logRecord);
            throw ex;
        }
    }

    private void fillRequestInfo(OperationLogRecord logRecord, Object[] args) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logRecord.setMethod(request.getMethod());
            logRecord.setRequestUri(request.getRequestURI());
            logRecord.setIpAddress(request.getRemoteAddr());
        }
        try {
            logRecord.setRequestData(objectMapper.writeValueAsString(args));
        } catch (JsonProcessingException ex) {
            logRecord.setRequestData("[]");
        }
    }
}
