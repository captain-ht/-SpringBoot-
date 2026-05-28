package com.library.modules.reader.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.common.result.PageResult;
import com.library.dto.ReaderQuery;
import com.library.dto.ReaderSaveRequest;
import com.library.modules.reader.entity.Reader;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

public interface ReaderService extends IService<Reader> {
    PageResult<Reader> pageReaders(ReaderQuery query);
    void saveReader(ReaderSaveRequest request);
    void importReaders(MultipartFile file);
    void exportReaders(HttpServletResponse response);
}
