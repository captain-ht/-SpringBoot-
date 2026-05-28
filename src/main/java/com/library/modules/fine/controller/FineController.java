package com.library.modules.fine.controller;

import com.library.common.annotation.OperationLog;
import com.library.common.result.ApiResult;
import com.library.common.result.PageResult;
import com.library.dto.FinePayRequest;
import com.library.dto.FineQuery;
import com.library.modules.fine.service.FineService;
import com.library.vo.FineVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;

    public FineController(FineService fineService) {
        this.fineService = fineService;
    }

    @Operation(summary = "罚款分页查询")
    @GetMapping
    public ApiResult<PageResult<FineVO>> page(FineQuery query) {
        return ApiResult.success(fineService.pageFines(query));
    }

    @Operation(summary = "缴纳罚款")
    @PostMapping("/pay")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @OperationLog(module = "罚款管理", action = "缴纳罚款")
    public ApiResult<Void> pay(@Valid @RequestBody FinePayRequest request) {
        fineService.pay(request);
        return ApiResult.success("缴费成功");
    }

    @Operation(summary = "导出罚款记录")
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public void export(HttpServletResponse response) {
        fineService.exportFines(response);
    }
}
