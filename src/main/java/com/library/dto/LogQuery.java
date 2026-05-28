package com.library.dto;

import lombok.Data;

@Data
public class LogQuery extends PageQuery {
    private String module;
    private String action;
    private String username;
    private String status;
    private String startTime;
    private String endTime;
}
