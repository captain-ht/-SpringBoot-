package com.library.modules.fine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.exception.BusinessException;
import com.library.common.result.PageResult;
import com.library.dto.FinePayRequest;
import com.library.dto.FineQuery;
import com.library.modules.fine.entity.Fine;
import com.library.modules.fine.mapper.FineMapper;
import com.library.modules.fine.service.FineService;
import com.library.vo.FineExportVO;
import com.library.vo.FineVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class FineServiceImpl extends ServiceImpl<FineMapper, Fine> implements FineService {

    @Override
    public PageResult<FineVO> pageFines(FineQuery query) {
        Page<FineVO> page = new Page<>(query.getCurrent(), query.getSize());
        return PageResult.of(baseMapper.selectFinePage(page, query));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(FinePayRequest request) {
        Fine fine = getById(request.getFineId());
        if (fine == null) {
            throw new BusinessException("罚款记录不存在");
        }
        fine.setStatus(1);
        fine.setPayType(request.getPayType());
        fine.setPayTime(LocalDateTime.now());
        updateById(fine);
    }

    @Override
    public BigDecimal getUnpaidAmount() {
        return list(new LambdaQueryWrapper<Fine>().eq(Fine::getStatus, 0))
                .stream()
                .map(Fine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void exportFines(HttpServletResponse response) {
        try {
            java.util.List<FineExportVO> rows = baseMapper.selectFinePage(new Page<>(1, Long.MAX_VALUE), new FineQuery())
                    .getRecords()
                    .stream()
                    .map(item -> {
                        FineExportVO vo = new FineExportVO();
                        vo.setReaderName(item.getReaderName());
                        vo.setReaderNo(item.getReaderNo());
                        vo.setBookTitle(item.getBookTitle());
                        vo.setBookCode(item.getBookCode());
                        vo.setAmount(String.valueOf(item.getAmount()));
                        vo.setReason(item.getReason());
                        vo.setStatusText(item.getStatus() != null && item.getStatus() == 1 ? "已支付" : "未支付");
                        vo.setPayType(item.getPayType());
                        vo.setPayTime(String.valueOf(item.getPayTime()));
                        return vo;
                    }).collect(Collectors.toList());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String fileName = URLEncoder.encode("罚款记录", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), FineExportVO.class).sheet("罚款").doWrite(rows);
        } catch (Exception ex) {
            throw new RuntimeException("导出罚款记录失败：" + ex.getMessage(), ex);
        }
    }
}
