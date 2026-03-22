package com.kds.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 请假审批 DTO
 */
@Data
public class LeaveApproveDTO {

    @NotNull(message = "请假ID不能为空")
    private Long id;

    @NotNull(message = "审批状态不能为空")
    private Integer auditStatus;

    private String teacherRemark;
}
