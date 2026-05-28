package com.library.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardVO {
    private Long totalBooks;
    private Long availableBooks;
    private Long totalReaders;
    private Long borrowingCount;
    private Long overdueCount;
    private BigDecimal unpaidFineAmount;
    private List<SimpleNameValueVO> categoryStats;
    private List<SimpleNameValueVO> monthlyBorrowStats;
    private List<SimpleNameValueVO> hotBookStats;
}
