package com.library.modules.dashboard.controller;

import com.library.common.result.ApiResult;
import com.library.modules.dashboard.service.DashboardService;
import com.library.vo.DashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "首页统计")
    @GetMapping
    public ApiResult<DashboardVO> dashboard() {
        return ApiResult.success(dashboardService.getDashboard());
    }
}
