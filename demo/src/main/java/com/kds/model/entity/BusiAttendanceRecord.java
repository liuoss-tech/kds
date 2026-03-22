package com.kds.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("busi_attendance_record")
public class BusiAttendanceRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private Long studentId;

    @TableField("class_id")
    private Long classId;

    @TableField("record_date")
    private LocalDate recordDate;

    @TableField("status")
    private Integer status;

    @TableField("leave_application_id")
    private Long leaveApplicationId;

    @TableField("creator_id")
    private Long creatorId;

    @TableField("sign_in_time")
    private LocalDateTime signInTime;

    @TableField("sign_out_time")
    private LocalDateTime signOutTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
