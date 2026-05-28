package com.library.modules.book.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.result.PageResult;
import com.library.dto.BookQuery;
import com.library.dto.BookSaveRequest;
import com.library.modules.book.entity.Book;
import com.library.modules.book.mapper.BookMapper;
import com.library.modules.book.service.BookService;
import com.library.vo.BookExportVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Override
    public PageResult<Book> pageBooks(BookQuery query) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>()
                .eq(query.getCategoryId() != null, Book::getCategoryId, query.getCategoryId())
                .eq(query.getStatus() != null, Book::getStatus, query.getStatus())
                .and(StringUtils.hasText(query.getKeyword()), w -> w
                        .like(Book::getTitle, query.getKeyword())
                        .or()
                        .like(Book::getAuthor, query.getKeyword())
                        .or()
                        .like(Book::getIsbn, query.getKeyword())
                        .or()
                        .like(Book::getBookCode, query.getKeyword()))
                .orderByDesc(Book::getId);
        Page<Book> page = page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
        return PageResult.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBook(BookSaveRequest request) {
        Book book = new Book();
        BeanUtils.copyProperties(request, book);
        if (request.getId() == null) {
            book.setAvailableStock(request.getTotalStock());
            book.setBorrowedCount(0);
        } else {
            Book dbBook = getById(request.getId());
            if (dbBook != null) {
                int borrowed = dbBook.getBorrowedCount() == null ? 0 : dbBook.getBorrowedCount();
                book.setAvailableStock(Math.max(request.getTotalStock() - borrowed, 0));
                book.setBorrowedCount(borrowed);
            }
        }
        if (book.getStatus() == null) {
            book.setStatus(1);
        }
        saveOrUpdate(book);
    }

    @Override
    public void exportBooks(HttpServletResponse response) {
        try {
            List<BookExportVO> books = list().stream().map(book -> {
                BookExportVO vo = new BookExportVO();
                BeanUtils.copyProperties(book, vo);
                return vo;
            }).collect(Collectors.toList());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String fileName = URLEncoder.encode("图书列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), BookExportVO.class).sheet("图书").doWrite(books);
        } catch (Exception ex) {
            throw new RuntimeException("导出失败：" + ex.getMessage(), ex);
        }
    }
}
