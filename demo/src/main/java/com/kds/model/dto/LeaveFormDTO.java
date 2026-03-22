package com.kds.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 请假表单 DTO（新增/编辑）
 */
@Data
public class LeaveFormDTO {

    private Long id;

    @NotNull(message = "请选择幼儿")
    private Long studentId;

    @NotNull(message = "请选择请假类型")
    private Integer leaveType;

    @NotNull(message = "请选择开始日期")
    private LocalDate startDate;

    @NotNull(message = "请选择结束日期")
    private LocalDate endDate;

    @NotBlank(message = "请填写请假原因")
    private String reason;

    private String proofUrl;
}
