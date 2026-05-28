package com.library.modules.reservation.controller;

import com.library.common.annotation.OperationLog;
import com.library.common.result.ApiResult;
import com.library.common.result.PageResult;
import com.library.dto.CancelReservationRequest;
import com.library.dto.ReservationQuery;
import com.library.dto.ReservationRequest;
import com.library.modules.reservation.service.ReservationService;
import com.library.vo.ReservationVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "预约记录分页查询")
    @GetMapping
    public ApiResult<PageResult<ReservationVO>> page(ReservationQuery query) {
        return ApiResult.success(reservationService.pageReservations(query));
    }

    @Operation(summary = "创建预约")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @OperationLog(module = "预约管理", action = "创建预约")
    public ApiResult<Void> create(@Valid @RequestBody ReservationRequest request) {
        reservationService.createReservation(request);
        return ApiResult.success("预约成功");
    }

    @Operation(summary = "取消预约")
    @PostMapping("/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @OperationLog(module = "预约管理", action = "取消预约")
    public ApiResult<Void> cancel(@Valid @RequestBody CancelReservationRequest request) {
        reservationService.cancelReservation(request);
        return ApiResult.success("取消成功");
    }

    @Operation(summary = "查询图书当前预约数")
    @GetMapping("/book/{bookId}/count")
    public ApiResult<Long> countByBook(@PathVariable Long bookId) {
        return ApiResult.success(reservationService.countActiveReservations(bookId));
    }
}
