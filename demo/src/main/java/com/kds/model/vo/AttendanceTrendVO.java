package com.kds.model.vo;

import lombok.Data;

@Data
public class AttendanceTrendVO {

    private String date;

    private Integer shouldAttend;

    private Integer present;

    private Double attendanceRate;
}