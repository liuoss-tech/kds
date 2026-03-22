package com.kds.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceVO {

    private Long id;

    private Long studentId;

    private String studentName;

    private Long classId;

    private String className;

    private LocalDate recordDate;

    private Integer status;

    private String statusText;

    private LocalDateTime signInTime;

    private LocalDateTime signOutTime;

    private Integer leaveType;

    private String leaveTypeText;

    private Long leaveApplicationId;

    private LocalDateTime createTime;
}
