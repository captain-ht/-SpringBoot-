package com.library.modules.borrow.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.dto.BorrowQuery;
import com.library.modules.borrow.entity.BorrowRecord;
import com.library.vo.BorrowRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
    IPage<BorrowRecordVO> selectBorrowRecordPage(Page<BorrowRecordVO> page, @Param("query") BorrowQuery query);
}
