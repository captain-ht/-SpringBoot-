package com.library.modules.book.controller;

import com.library.common.result.ApiResult;
import com.library.common.result.PageResult;
import com.library.dto.BookQuery;
import com.library.dto.BookSaveRequest;
import com.library.modules.book.entity.Book;
import com.library.modules.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "图书分页查询")
    @GetMapping
    public ApiResult<PageResult<Book>> page(BookQuery query) {
        return ApiResult.success(bookService.pageBooks(query));
    }

    @Operation(summary = "保存图书")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResult<Void> save(@Valid @RequestBody BookSaveRequest request) {
        bookService.saveBook(request);
        return ApiResult.success("保存成功");
    }

    @Operation(summary = "删除图书")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Void> delete(@PathVariable Long id) {
        bookService.removeById(id);
        return ApiResult.success("删除成功");
    }

    @Operation(summary = "导出图书")
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public void export(HttpServletResponse response) {
        bookService.exportBooks(response);
    }
}
