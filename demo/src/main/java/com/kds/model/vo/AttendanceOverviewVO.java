package com.kds.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class AttendanceOverviewVO {

    private Integer totalShouldAttend;

    private Integer totalPresent;

    private Integer totalAbsent;

    private Integer totalLeave;

    private Double attendanceRate;

    private Double presentRate;

    private Double absentRate;

    private Double leaveRate;

    private List<StatusDistributionVO> statusDistribution;
}