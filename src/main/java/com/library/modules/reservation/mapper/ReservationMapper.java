package com.library.modules.reservation.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.ReservationQuery;
import com.library.modules.reservation.entity.Reservation;
import com.library.vo.ReservationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
    IPage<ReservationVO> selectReservationPage(Page<ReservationVO> page, @Param("query") ReservationQuery query);
}
