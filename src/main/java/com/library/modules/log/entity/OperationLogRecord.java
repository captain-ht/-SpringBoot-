package com.library.modules.log.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("sys_operation_log")
@EqualsAndHashCode(callSuper = true)
public class OperationLogRecord extends BaseEntity {
    @TableId
    private Long id;
    private String module;
    private String action;
    private String username;
    private String method;
    private String requestUri;
    private String ipAddress;
    private String requestData;
    private String status;
    private String errorMessage;
}
