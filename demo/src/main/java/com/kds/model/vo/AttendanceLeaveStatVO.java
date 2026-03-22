package com.kds.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class AttendanceLeaveStatVO {

    private Integer totalLeave;

    private List<LeaveDistributionVO> leaveDistribution;
}