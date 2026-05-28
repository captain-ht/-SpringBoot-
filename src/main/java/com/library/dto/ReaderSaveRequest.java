package com.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReaderSaveRequest {
    private Long id;

    @NotBlank(message = "读者编号不能为空")
    private String readerNo;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotNull(message = "读者类型不能为空")
    private Integer readerType;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Email(message = "邮箱格式错误")
    private String email;

    private String gender;
    private String department;
    private LocalDate registerDate;
    private Integer maxBorrowCount;
    private Integer borrowDays;
    private Integer status;
}
