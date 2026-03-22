package com.kds.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假记录 VO
 */
@Data
public class LeaveVO {

    private Long id;

    private Long studentId;

    private String studentName;

    private Long classId;

    private String className;

    private Integer leaveType;

    private String leaveTypeText;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer leaveDays;

    private String reason;

    private String proofUrl;

    private Integer auditStatus;

    private String auditStatusText;

    private Long teacherId;

    private String teacherName;

    private LocalDateTime teacherAuditTime;

    private String teacherRemark;

    private Long adminId;

    private String adminName;

    private LocalDateTime adminAuditTime;

    private String adminRemark;

    private Long parentUserId;

    private String parentUserName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
