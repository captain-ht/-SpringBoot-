package com.library.modules.reader.controller;

import com.library.common.result.ApiResult;
import com.library.common.result.PageResult;
import com.library.dto.ReaderQuery;
import com.library.dto.ReaderSaveRequest;
import com.library.modules.reader.entity.Reader;
import com.library.modules.reader.service.ReaderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @Operation(summary = "读者分页查询")
    @GetMapping
    public ApiResult<PageResult<Reader>> page(ReaderQuery query) {
        return ApiResult.success(readerService.pageReaders(query));
    }

    @Operation(summary = "保存读者")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResult<Void> save(@Valid @RequestBody ReaderSaveRequest request) {
        readerService.saveReader(request);
        return ApiResult.success("保存成功");
    }

    @Operation(summary = "导入读者")
    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ApiResult<Void> importReaders(@RequestParam("file") MultipartFile file) {
        readerService.importReaders(file);
        return ApiResult.success("导入成功");
    }

    @Operation(summary = "导出读者")
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public void exportReaders(HttpServletResponse response) {
        readerService.exportReaders(response);
    }

    @Operation(summary = "删除读者")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Void> delete(@PathVariable Long id) {
        readerService.removeById(id);
        return ApiResult.success("删除成功");
    }
}
