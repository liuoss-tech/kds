package com.kds.model.vo;

import lombok.Data;

@Data
public class LeaveDistributionVO {

    private Integer leaveType;

    private String leaveTypeText;

    private Integer count;

    private Double rate;
}