package com.library.modules.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.modules.book.entity.Book;
import com.library.modules.book.service.BookService;
import com.library.modules.borrow.entity.BorrowRecord;
import com.library.modules.borrow.service.BorrowService;
import com.library.modules.category.entity.Category;
import com.library.modules.category.service.CategoryService;
import com.library.modules.fine.service.FineService;
import com.library.modules.reader.service.ReaderService;
import com.library.vo.DashboardVO;
import com.library.vo.SimpleNameValueVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements com.library.modules.dashboard.service.DashboardService {

    private final BookService bookService;
    private final ReaderService readerService;
    private final BorrowService borrowService;
    private final FineService fineService;
    private final CategoryService categoryService;

    public DashboardServiceImpl(BookService bookService,
                                ReaderService readerService,
                                BorrowService borrowService,
                                FineService fineService,
                                CategoryService categoryService) {
        this.bookService = bookService;
        this.readerService = readerService;
        this.borrowService = borrowService;
        this.fineService = fineService;
        this.categoryService = categoryService;
    }

    @Override
    public DashboardVO getDashboard() {
        List<SimpleNameValueVO> categoryStats = new ArrayList<>();
        for (Category category : categoryService.listAll()) {
            long count = bookService.count(new LambdaQueryWrapper<Book>().eq(Book::getCategoryId, category.getId()));
            categoryStats.add(new SimpleNameValueVO(category.getName(), count));
        }

        List<SimpleNameValueVO> monthlyBorrowStats = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 5; i >= 0; i--) {
            LocalDate firstDay = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
            long count = borrowService.count(new LambdaQueryWrapper<BorrowRecord>()
                    .between(BorrowRecord::getBorrowDate, firstDay, lastDay));
            monthlyBorrowStats.add(new SimpleNameValueVO(firstDay.format(formatter), count));
        }

        List<SimpleNameValueVO> hotBookStats = bookService.list().stream()
                .sorted((a, b) -> Integer.compare(
                        b.getBorrowedCount() == null ? 0 : b.getBorrowedCount(),
                        a.getBorrowedCount() == null ? 0 : a.getBorrowedCount()))
                .limit(5)
                .map(book -> new SimpleNameValueVO(book.getTitle(), Long.valueOf(book.getBorrowedCount() == null ? 0 : book.getBorrowedCount())))
                .toList();

        return DashboardVO.builder()
                .totalBooks(bookService.count())
                .availableBooks(bookService.list().stream().mapToLong(book -> book.getAvailableStock() == null ? 0 : book.getAvailableStock()).sum())
                .totalReaders(readerService.count())
                .borrowingCount(borrowService.count(new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getStatus, 0)))
                .overdueCount(borrowService.count(new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getStatus, 0)
                        .lt(BorrowRecord::getDueDate, LocalDate.now())))
                .unpaidFineAmount(fineService.getUnpaidAmount())
                .categoryStats(categoryStats)
                .monthlyBorrowStats(monthlyBorrowStats)
                .hotBookStats(hotBookStats)
                .build();
    }
}
