package com.library.modules.reservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.exception.BusinessException;
import com.library.common.result.PageResult;
import com.library.dto.CancelReservationRequest;
import com.library.dto.ReservationQuery;
import com.library.dto.ReservationRequest;
import com.library.modules.book.entity.Book;
import com.library.modules.book.service.BookService;
import com.library.modules.reader.entity.Reader;
import com.library.modules.reader.service.ReaderService;
import com.library.modules.reservation.entity.Reservation;
import com.library.modules.reservation.mapper.ReservationMapper;
import com.library.modules.reservation.service.ReservationService;
import com.library.vo.ReservationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    private final ReaderService readerService;
    private final BookService bookService;

    public ReservationServiceImpl(ReaderService readerService, BookService bookService) {
        this.readerService = readerService;
        this.bookService = bookService;
    }

    @Override
    public PageResult<ReservationVO> pageReservations(ReservationQuery query) {
        Page<ReservationVO> page = new Page<>(query.getCurrent(), query.getSize());
        return PageResult.of(baseMapper.selectReservationPage(page, query));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createReservation(ReservationRequest request) {
        Reader reader = readerService.getById(request.getReaderId());
        if (reader == null || reader.getStatus() == null || reader.getStatus() != 1) {
            throw new BusinessException("读者不存在或已停用");
        }
        Book book = bookService.getById(request.getBookId());
        if (book == null || book.getStatus() == null || book.getStatus() != 1) {
            throw new BusinessException("图书不存在或不可预约");
        }
        Reservation existed = getOne(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getReaderId, request.getReaderId())
                .eq(Reservation::getBookId, request.getBookId())
                .eq(Reservation::getStatus, 0)
                .last("limit 1"));
        if (existed != null) {
            throw new BusinessException("该读者已存在未完成预约");
        }
        Reservation reservation = new Reservation();
        reservation.setReaderId(request.getReaderId());
        reservation.setBookId(request.getBookId());
        reservation.setReservationDate(LocalDate.now());
        reservation.setExpireDate(LocalDate.now().plusDays(3));
        reservation.setStatus(0);
        save(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(CancelReservationRequest request) {
        Reservation reservation = getById(request.getReservationId());
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }
        reservation.setStatus(2);
        updateById(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeReservation(Long readerId, Long bookId) {
        Reservation reservation = getOne(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getReaderId, readerId)
                .eq(Reservation::getBookId, bookId)
                .eq(Reservation::getStatus, 0)
                .orderByAsc(Reservation::getId)
                .last("limit 1"));
        if (reservation != null) {
            reservation.setStatus(1);
            updateById(reservation);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void expireReservations() {
        lambdaUpdate()
                .eq(Reservation::getStatus, 0)
                .lt(Reservation::getExpireDate, LocalDate.now())
                .set(Reservation::getStatus, 3)
                .update();
    }

    @Override
    public long countActiveReservations(Long bookId) {
        return count(new LambdaQueryWrapper<Reservation>()
                .eq(Reservation::getBookId, bookId)
                .eq(Reservation::getStatus, 0));
    }
}
