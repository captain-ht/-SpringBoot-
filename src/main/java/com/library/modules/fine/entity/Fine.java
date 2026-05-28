package com.library.modules.fine.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("library_fine")
@EqualsAndHashCode(callSuper = true)
public class Fine extends BaseEntity {
    @TableId
    private Long id;
    private Long borrowRecordId;
    private Long readerId;
    private BigDecimal amount;
    private String reason;
    private Integer status;
    private String payType;
    private LocalDateTime payTime;
}
