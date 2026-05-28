package com.library.modules.borrow.controller;

import com.library.common.annotation.OperationLog;
import com.library.common.result.ApiResult;
import com.library.common.result.PageResult;
import com.library.dto.BorrowQuery;
import com.library.dto.BorrowRequest;
import com.library.dto.RenewBorrowRequest;
import com.library.dto.ReturnRequest;
import com.library.modules.borrow.service.BorrowService;
import com.library.vo.BorrowRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @Operation(summary = "借阅记录分页查询")
    @GetMapping
    public ApiResult<PageResult<BorrowRecordVO>> page(BorrowQuery query) {
        return ApiResult.success(borrowService.pageBorrowRecords(query));
    }

    @Operation(summary = "借书")
    @PostMapping("/borrow")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @OperationLog(module = "借阅管理", action = "借书")
    public ApiResult<Void> borrow(@Valid @RequestBody BorrowRequest request) {
        borrowService.borrowBook(request);
        return ApiResult.success("借阅成功");
    }

    @Operation(summary = "还书")
    @PostMapping("/return")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @OperationLog(module = "借阅管理", action = "还书")
    public ApiResult<Void> returnBook(@Valid @RequestBody ReturnRequest request) {
        borrowService.returnBook(request);
        return ApiResult.success("归还成功");
    }

    @Operation(summary = "续借")
    @PostMapping("/renew")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @OperationLog(module = "借阅管理", action = "续借")
    public ApiResult<Void> renew(@Valid @RequestBody RenewBorrowRequest request) {
        borrowService.renewBorrow(request);
        return ApiResult.success("续借成功");
    }

    @Operation(summary = "导出借阅记录")
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public void export(HttpServletResponse response) {
        borrowService.exportBorrows(response);
    }
}
