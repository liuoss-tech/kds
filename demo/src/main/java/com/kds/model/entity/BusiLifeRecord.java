package com.kds.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("busi_life_record")
public class BusiLifeRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private Long studentId;

    @TableField("class_id")
    private Long classId;

    @TableField("record_date")
    private LocalDate recordDate;

    @TableField("lunch_intake")
    private Integer lunchIntake;

    @TableField("water_count")
    private Integer waterCount;

    @TableField("sleep_start_time")
    private LocalTime sleepStartTime;

    @TableField("sleep_duration")
    private Integer sleepDuration;

    @TableField("sleep_quality")
    private Integer sleepQuality;

    @TableField("toilet_count")
    private Integer toiletCount;

    @TableField("toilet_abnormal")
    private String toiletAbnormal;

    @TableField("morning_temp")
    private BigDecimal morningTemp;

    @TableField("afternoon_temp")
    private BigDecimal afternoonTemp;

    @TableField("health_symptoms")
    private String healthSymptoms;

    @TableField("teacher_remark")
    private String teacherRemark;

    @TableField("creator_id")
    private Long creatorId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}