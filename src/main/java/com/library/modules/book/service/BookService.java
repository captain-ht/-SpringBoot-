package com.library.modules.book.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.result.PageResult;
import com.library.dto.BookQuery;
import com.library.dto.BookSaveRequest;
import com.library.modules.book.entity.Book;
import jakarta.servlet.http.HttpServletResponse;

public interface BookService extends IService<Book> {
    PageResult<Book> pageBooks(BookQuery query);
    void saveBook(BookSaveRequest request);
    void exportBooks(HttpServletResponse response);
}
