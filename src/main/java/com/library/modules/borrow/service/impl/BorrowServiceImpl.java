package com.library.modules.borrow.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.exception.BusinessException;
import com.library.common.result.PageResult;
import com.library.dto.BorrowQuery;
import com.library.dto.BorrowRequest;
import com.library.dto.RenewBorrowRequest;
import com.library.dto.ReturnRequest;
import com.library.modules.book.entity.Book;
import com.library.modules.book.service.BookService;
import com.library.modules.borrow.entity.BorrowRecord;
import com.library.modules.borrow.mapper.BorrowRecordMapper;
import com.library.modules.borrow.service.BorrowService;
import com.library.modules.fine.entity.Fine;
import com.library.modules.fine.service.FineService;
import com.library.modules.reader.entity.Reader;
import com.library.modules.reader.service.ReaderService;
import com.library.modules.reservation.service.ReservationService;
import com.library.vo.BorrowExportVO;
import com.library.vo.BorrowRecordVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowService {

    private static final BigDecimal DAILY_FINE = new BigDecimal("0.50");

    private final ReaderService readerService;
    private final BookService bookService;
    private final FineService fineService;
    private final ReservationService reservationService;

    public BorrowServiceImpl(ReaderService readerService,
                             BookService bookService,
                             FineService fineService,
                             ReservationService reservationService) {
        this.readerService = readerService;
        this.bookService = bookService;
        this.fineService = fineService;
        this.reservationService = reservationService;
    }

    @Override
    public PageResult<BorrowRecordVO> pageBorrowRecords(BorrowQuery query) {
        Page<BorrowRecordVO> page = new Page<>(query.getCurrent(), query.getSize());
        return PageResult.of(baseMapper.selectBorrowRecordPage(page, query));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrowBook(BorrowRequest request) {
        Reader reader = readerService.getById(request.getReaderId());
        if (reader == null || reader.getStatus() == null || reader.getStatus() != 1) {
            throw new BusinessException("读者不存在或已停用");
        }
        Book book = bookService.getById(request.getBookId());
        if (book == null || book.getStatus() == null || book.getStatus() != 1) {
            throw new BusinessException("图书不存在或不可借阅");
        }
        if (book.getAvailableStock() == null || book.getAvailableStock() <= 0) {
            throw new BusinessException("当前图书库存不足");
        }
        long activeReservations = reservationService.countActiveReservations(book.getId());
        if (activeReservations > 0) {
            throw new BusinessException("该图书当前存在预约记录，请优先处理预约");
        }
        long currentBorrowing = count(new LambdaQueryWrapper<BorrowRecord>()
                .eq(BorrowRecord::getReaderId, reader.getId())
                .eq(BorrowRecord::getStatus, 0));
        if (currentBorrowing >= reader.getMaxBorrowCount()) {
            throw new BusinessException("已达到最大借阅数量");
        }

        BorrowRecord record = new BorrowRecord();
        record.setReaderId(reader.getId());
        record.setBookId(book.getId());
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(reader.getBorrowDays()));
        record.setRenewCount(0);
        record.setStatus(0);
        save(record);

        book.setAvailableStock(book.getAvailableStock() - 1);
        book.setBorrowedCount((book.getBorrowedCount() == null ? 0 : book.getBorrowedCount()) + 1);
        bookService.updateById(book);
        reservationService.completeReservation(reader.getId(), book.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(ReturnRequest request) {
        BorrowRecord record = getById(request.getBorrowRecordId());
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        if (record.getStatus() != null && record.getStatus() == 1) {
            throw new BusinessException("该图书已归还");
        }
        Book book = bookService.getById(record.getBookId());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }

        LocalDate today = LocalDate.now();
        long overdueDays = today.isAfter(record.getDueDate()) ? ChronoUnit.DAYS.between(record.getDueDate(), today) : 0;
        record.setReturnDate(today);
        record.setOverdueDays((int) overdueDays);
        record.setStatus(1);
        updateById(record);

        book.setAvailableStock((book.getAvailableStock() == null ? 0 : book.getAvailableStock()) + 1);
        bookService.updateById(book);

        if (overdueDays > 0) {
            Fine fine = new Fine();
            fine.setBorrowRecordId(record.getId());
            fine.setReaderId(record.getReaderId());
            fine.setAmount(DAILY_FINE.multiply(BigDecimal.valueOf(overdueDays)));
            fine.setReason("图书逾期归还");
            fine.setStatus(0);
            fineService.save(fine);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void renewBorrow(RenewBorrowRequest request) {
        BorrowRecord record = getById(request.getBorrowRecordId());
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        if (record.getStatus() == null || record.getStatus() != 0) {
            throw new BusinessException("仅借阅中的记录支持续借");
        }
        if (record.getDueDate() != null && record.getDueDate().isBefore(LocalDate.now())) {
            throw new BusinessException("已逾期记录不允许续借");
        }
        int renewCount = record.getRenewCount() == null ? 0 : record.getRenewCount();
        if (renewCount >= 1) {
            throw new BusinessException("每条借阅记录仅允许续借 1 次");
        }
        Reader reader = readerService.getById(record.getReaderId());
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        record.setDueDate(record.getDueDate().plusDays(reader.getBorrowDays()));
        record.setRenewCount(renewCount + 1);
        updateById(record);
    }

    @Override
    public void exportBorrows(HttpServletResponse response) {
        try {
            List<BorrowExportVO> rows = baseMapper.selectBorrowRecordPage(new Page<>(1, Long.MAX_VALUE), new BorrowQuery())
                    .getRecords()
                    .stream()
                    .map(item -> {
                        BorrowExportVO vo = new BorrowExportVO();
                        vo.setReaderName(item.getReaderName());
                        vo.setReaderNo(item.getReaderNo());
                        vo.setBookTitle(item.getBookTitle());
                        vo.setBookCode(item.getBookCode());
                        vo.setAuthor(item.getAuthor());
                        vo.setBorrowDate(String.valueOf(item.getBorrowDate()));
                        vo.setDueDate(String.valueOf(item.getDueDate()));
                        vo.setReturnDate(String.valueOf(item.getReturnDate()));
                        vo.setOverdueDays(item.getOverdueDays());
                        vo.setRenewCount(item.getRenewCount());
                        return vo;
                    }).collect(Collectors.toList());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String fileName = URLEncoder.encode("借阅记录", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), BorrowExportVO.class).sheet("借阅").doWrite(rows);
        } catch (Exception ex) {
            throw new RuntimeException("导出借阅记录失败：" + ex.getMessage(), ex);
        }
    }
}
