package com.library.modules.reservation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.result.PageResult;
import com.library.dto.CancelReservationRequest;
import com.library.dto.ReservationQuery;
import com.library.dto.ReservationRequest;
import com.library.modules.reservation.entity.Reservation;
import com.library.vo.ReservationVO;

public interface ReservationService extends IService<Reservation> {
    PageResult<ReservationVO> pageReservations(ReservationQuery query);
    void createReservation(ReservationRequest request);
    void cancelReservation(CancelReservationRequest request);
    void completeReservation(Long readerId, Long bookId);
    void expireReservations();
    long countActiveReservations(Long bookId);
}
