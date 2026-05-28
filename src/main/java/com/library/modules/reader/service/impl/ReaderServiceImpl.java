package com.library.modules.reader.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.result.PageResult;
import com.library.dto.ReaderQuery;
import com.library.dto.ReaderSaveRequest;
import com.library.modules.reader.entity.Reader;
import com.library.modules.reader.mapper.ReaderMapper;
import com.library.modules.reader.service.ReaderService;
import com.library.vo.ReaderExportVO;
import com.library.vo.ReaderImportVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReaderServiceImpl extends ServiceImpl<ReaderMapper, Reader> implements ReaderService {

    @Override
    public PageResult<Reader> pageReaders(ReaderQuery query) {
        LambdaQueryWrapper<Reader> wrapper = new LambdaQueryWrapper<Reader>()
                .eq(query.getStatus() != null, Reader::getStatus, query.getStatus())
                .and(StringUtils.hasText(query.getKeyword()), w -> w
                        .like(Reader::getName, query.getKeyword())
                        .or()
                        .like(Reader::getReaderNo, query.getKeyword())
                        .or()
                        .like(Reader::getPhone, query.getKeyword()))
                .orderByDesc(Reader::getId);
        return PageResult.of(page(new Page<>(query.getCurrent(), query.getSize()), wrapper));
    }

    @Override
    public void saveReader(ReaderSaveRequest request) {
        Reader reader = new Reader();
        BeanUtils.copyProperties(request, reader);
        if (reader.getRegisterDate() == null) {
            reader.setRegisterDate(java.time.LocalDate.now());
        }
        if (reader.getMaxBorrowCount() == null) {
            reader.setMaxBorrowCount(5);
        }
        if (reader.getBorrowDays() == null) {
            reader.setBorrowDays(30);
        }
        if (reader.getStatus() == null) {
            reader.setStatus(1);
        }
        saveOrUpdate(reader);
    }

    @Override
    public void importReaders(MultipartFile file) {
        try {
            List<ReaderImportVO> rows = EasyExcel.read(file.getInputStream())
                    .head(ReaderImportVO.class)
                    .sheet()
                    .doReadSync();
            for (ReaderImportVO row : rows) {
                Reader reader = new Reader();
                BeanUtils.copyProperties(row, reader);
                reader.setRegisterDate(java.time.LocalDate.now());
                reader.setStatus(1);
                saveOrUpdate(reader, new LambdaQueryWrapper<Reader>().eq(Reader::getReaderNo, reader.getReaderNo()));
            }
        } catch (Exception ex) {
            throw new RuntimeException("导入读者失败：" + ex.getMessage(), ex);
        }
    }

    @Override
    public void exportReaders(HttpServletResponse response) {
        try {
            List<ReaderExportVO> rows = list().stream().map(item -> {
                ReaderExportVO vo = new ReaderExportVO();
                BeanUtils.copyProperties(item, vo);
                return vo;
            }).collect(Collectors.toList());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String fileName = URLEncoder.encode("读者列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ReaderExportVO.class).sheet("读者").doWrite(rows);
        } catch (Exception ex) {
            throw new RuntimeException("导出读者失败：" + ex.getMessage(), ex);
        }
    }
}
