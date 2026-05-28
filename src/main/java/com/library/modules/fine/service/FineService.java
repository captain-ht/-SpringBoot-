package com.library.modules.fine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.result.PageResult;
import com.library.dto.FinePayRequest;
import com.library.dto.FineQuery;
import com.library.modules.fine.entity.Fine;
import com.library.vo.FineVO;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;

public interface FineService extends IService<Fine> {
    PageResult<FineVO> pageFines(FineQuery query);
    void pay(FinePayRequest request);
    BigDecimal getUnpaidAmount();
    void exportFines(HttpServletResponse response);
}
