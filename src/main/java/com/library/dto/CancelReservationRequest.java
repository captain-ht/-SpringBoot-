package com.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CancelReservationRequest {
    @NotNull(message = "预约记录不能为空")
    private Long reservationId;
}
