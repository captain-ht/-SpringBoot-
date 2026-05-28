package com.library.modules.borrow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.result.PageResult;
import com.library.dto.BorrowQuery;
import com.library.dto.BorrowRequest;
import com.library.dto.RenewBorrowRequest;
import com.library.dto.ReturnRequest;
import com.library.modules.borrow.entity.BorrowRecord;
import com.library.vo.BorrowRecordVO;
import jakarta.servlet.http.HttpServletResponse;

public interface BorrowService extends IService<BorrowRecord> {
    PageResult<BorrowRecordVO> pageBorrowRecords(BorrowQuery query);
    void borrowBook(BorrowRequest request);
    void returnBook(ReturnRequest request);
    void renewBorrow(RenewBorrowRequest request);
    void exportBorrows(HttpServletResponse response);
}
