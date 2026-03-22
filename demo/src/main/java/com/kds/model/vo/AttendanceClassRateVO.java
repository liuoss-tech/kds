package com.kds.model.vo;

import lombok.Data;

@Data
public class AttendanceClassRateVO {

    private Long classId;

    private String className;

    private Integer grade;

    private Integer studentCount;

    private Integer workDays;

    private Integer shouldAttend;

    private Integer present;

    private Integer absent;

    private Integer leave;

    private Double attendanceRate;
}