package com.kds.model.dto;

import lombok.Data;

/**
 * 请假查询 DTO
 */
@Data
public class LeaveQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private Integer status;

    private Long studentId;

    private String leaveType;
}
