package com.library.modules.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.exception.BusinessException;
import com.library.common.result.PageResult;
import com.library.dto.PurchaseReceiveRequest;
import com.library.dto.PurchaseQuery;
import com.library.dto.PurchaseSaveRequest;
import com.library.modules.book.entity.Book;
import com.library.modules.book.service.BookService;
import com.library.modules.purchase.entity.PurchaseOrder;
import com.library.modules.purchase.mapper.PurchaseOrderMapper;
import com.library.modules.purchase.service.PurchaseOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    private final BookService bookService;

    public PurchaseOrderServiceImpl(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public PageResult<PurchaseOrder> pageOrders(PurchaseQuery query) {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<PurchaseOrder>()
                .eq(query.getStatus() != null, PurchaseOrder::getStatus, query.getStatus())
                .and(StringUtils.hasText(query.getKeyword()), w -> w
                        .like(PurchaseOrder::getPurchaseNo, query.getKeyword())
                        .or()
                        .like(PurchaseOrder::getBookTitle, query.getKeyword())
                        .or()
                        .like(PurchaseOrder::getSupplier, query.getKeyword()))
                .orderByDesc(PurchaseOrder::getId);
        return PageResult.of(page(new Page<>(query.getCurrent(), query.getSize()), wrapper));
    }

    @Override
    public void saveOrder(PurchaseSaveRequest request) {
        PurchaseOrder order = new PurchaseOrder();
        BeanUtils.copyProperties(request, order);
        if (order.getStatus() == null) {
            order.setStatus(0);
        }
        saveOrUpdate(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveOrder(PurchaseReceiveRequest request) {
        PurchaseOrder order = getById(request.getPurchaseId());
        if (order == null) {
            throw new BusinessException("采购单不存在");
        }
        if (order.getStatus() != null && order.getStatus() == 2) {
            throw new BusinessException("该采购单已完成到货入库");
        }
        Book book = bookService.getOne(new LambdaQueryWrapper<Book>().eq(Book::getIsbn, order.getIsbn()).last("limit 1"));
        if (book == null) {
            book = new Book();
            book.setBookCode("AUTO-" + System.currentTimeMillis());
            book.setIsbn(order.getIsbn());
            book.setTitle(order.getBookTitle());
            book.setAuthor(order.getAuthor());
            book.setCategoryId(request.getCategoryId());
            book.setPublisher(order.getPublisher());
            book.setPrice(order.getUnitPrice());
            book.setTotalStock(order.getQuantity());
            book.setAvailableStock(order.getQuantity());
            book.setBorrowedCount(0);
            book.setStatus(1);
            book.setLocation(request.getLocation());
            book.setDescription(request.getDescription());
            book.setCoverUrl(request.getCoverUrl());
            bookService.save(book);
        } else {
            int quantity = order.getQuantity() == null ? 0 : order.getQuantity();
            book.setTotalStock((book.getTotalStock() == null ? 0 : book.getTotalStock()) + quantity);
            book.setAvailableStock((book.getAvailableStock() == null ? 0 : book.getAvailableStock()) + quantity);
            if (request.getCategoryId() != null) {
                book.setCategoryId(request.getCategoryId());
            }
            if (StringUtils.hasText(request.getLocation())) {
                book.setLocation(request.getLocation());
            }
            if (StringUtils.hasText(request.getDescription())) {
                book.setDescription(request.getDescription());
            }
            if (StringUtils.hasText(request.getCoverUrl())) {
                book.setCoverUrl(request.getCoverUrl());
            }
            bookService.updateById(book);
        }
        order.setStatus(2);
        updateById(order);
    }
}
