package com.kds.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假申请实体类
 * 对应数据库表：busi_leave_application
 */
@Data
@TableName("busi_leave_application")
public class BusiLeaveApplication {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private Long studentId;

    @TableField("class_id")
    private Long classId;

    @TableField("parent_user_id")
    private Long parentUserId;

    @TableField("leave_type")
    private Integer leaveType;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    @TableField("leave_days")
    private Integer leaveDays;

    @TableField("reason")
    private String reason;

    @TableField("proof_url")
    private String proofUrl;

    @TableField("audit_status")
    private Integer auditStatus;

    @TableField("teacher_id")
    private Long teacherId;

    @TableField("teacher_audit_time")
    private LocalDateTime teacherAuditTime;

    @TableField("teacher_remark")
    private String teacherRemark;

    @TableField("admin_id")
    private Long adminId;

    @TableField("admin_audit_time")
    private LocalDateTime adminAuditTime;

    @TableField("admin_remark")
    private String adminRemark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
