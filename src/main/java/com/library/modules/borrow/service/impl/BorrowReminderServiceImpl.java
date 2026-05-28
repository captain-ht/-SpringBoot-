package com.library.modules.borrow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.modules.borrow.entity.BorrowRecord;
import com.library.modules.borrow.service.BorrowReminderService;
import com.library.modules.borrow.service.BorrowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BorrowReminderServiceImpl implements BorrowReminderService {

    private final BorrowService borrowService;

    public BorrowReminderServiceImpl(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0 8 * * ?")
    public void scanAndMarkOverdueReminders() {
        List<BorrowRecord> overdueRecords = borrowService.list(new LambdaQueryWrapper<BorrowRecord>()
                .eq(BorrowRecord::getStatus, 0)
                .lt(BorrowRecord::getDueDate, LocalDate.now())
                .isNull(BorrowRecord::getRemindTime));
        if (overdueRecords.isEmpty()) {
            log.info("No overdue borrow records need reminders");
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (BorrowRecord record : overdueRecords) {
            record.setRemindTime(now);
        }
        borrowService.updateBatchById(overdueRecords);
        log.info("Marked {} overdue borrow records for reminder", overdueRecords.size());
    }
}
