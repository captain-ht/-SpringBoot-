package com.library.modules.fine.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.FineQuery;
import com.library.modules.fine.entity.Fine;
import com.library.vo.FineVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FineMapper extends BaseMapper<Fine> {
    IPage<FineVO> selectFinePage(Page<FineVO> page, @Param("query") FineQuery query);
}
