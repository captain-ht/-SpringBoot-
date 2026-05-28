package com.library.modules.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.modules.log.entity.OperationLogRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogRecordMapper extends BaseMapper<OperationLogRecord> {
}
