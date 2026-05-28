package com.library.modules.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.modules.book.entity.Book;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
