package com.library.modules.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("library_purchase_order")
@EqualsAndHashCode(callSuper = true)
public class PurchaseOrder extends BaseEntity {
    @TableId
    private Long id;
    private String purchaseNo;
    private String bookTitle;
    private String author;
    private String isbn;
    private String publisher;
    private Integer quantity;
    private BigDecimal unitPrice;
    private LocalDate expectedDate;
    private String supplier;
    private Integer status;
    private String remark;
}
