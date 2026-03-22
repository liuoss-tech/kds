package com.kds.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ParentLifeRecordVO {

    private Long id;

    private Long studentId;

    private String studentName;

    private Long classId;

    private String className;

    private LocalDate recordDate;

    private Integer lunchIntake;

    private String lunchIntakeText;

    private Integer waterCount;

    private String sleepStartTime;

    private Integer sleepDuration;

    private Integer sleepQuality;

    private String sleepQualityText;

    private Integer toiletCount;

    private String toiletAbnormal;

    private BigDecimal morningTemp;

    private BigDecimal afternoonTemp;

    private String healthSymptoms;

    private String teacherRemark;

    private LocalDateTime createTime;
}
