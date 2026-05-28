package com.library.modules.purchase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.result.PageResult;
import com.library.dto.PurchaseQuery;
import com.library.dto.PurchaseReceiveRequest;
import com.library.dto.PurchaseSaveRequest;
import com.library.modules.purchase.entity.PurchaseOrder;

public interface PurchaseOrderService extends IService<PurchaseOrder> {
    PageResult<PurchaseOrder> pageOrders(PurchaseQuery query);
    void saveOrder(PurchaseSaveRequest request);
    void receiveOrder(PurchaseReceiveRequest request);
}
