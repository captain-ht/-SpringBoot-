package com.library.modules.purchase.controller;

import com.library.common.result.ApiResult;
import com.library.common.result.PageResult;
import com.library.dto.PurchaseQuery;
import com.library.dto.PurchaseReceiveRequest;
import com.library.dto.PurchaseSaveRequest;
import com.library.modules.purchase.entity.PurchaseOrder;
import com.library.modules.purchase.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @Operation(summary = "采购单分页查询")
    @GetMapping
    public ApiResult<PageResult<PurchaseOrder>> page(PurchaseQuery query) {
        return ApiResult.success(purchaseOrderService.pageOrders(query));
    }

    @Operation(summary = "保存采购单")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResult<Void> save(@Valid @RequestBody PurchaseSaveRequest request) {
        purchaseOrderService.saveOrder(request);
        return ApiResult.success("保存成功");
    }

    @Operation(summary = "采购到货入库")
    @PostMapping("/receive")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResult<Void> receive(@Valid @RequestBody PurchaseReceiveRequest request) {
        purchaseOrderService.receiveOrder(request);
        return ApiResult.success("入库成功");
    }

    @Operation(summary = "删除采购单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Void> delete(@PathVariable Long id) {
        purchaseOrderService.removeById(id);
        return ApiResult.success("删除成功");
    }
}
