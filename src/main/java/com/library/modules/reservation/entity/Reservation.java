package com.library.modules.reservation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@TableName("library_reservation")
@EqualsAndHashCode(callSuper = true)
public class Reservation extends BaseEntity {
    @TableId
    private Long id;
    private Long readerId;
    private Long bookId;
    private LocalDate reservationDate;
    private LocalDate expireDate;
    private Integer status;
}
