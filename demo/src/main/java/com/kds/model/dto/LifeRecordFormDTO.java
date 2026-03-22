package com.kds.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LifeRecordFormDTO {

    @NotNull(message = "幼儿ID不能为空")
    private Long studentId;

    @NotNull(message = "班级ID不能为空")
    private Long classId;

    @NotNull(message = "记录日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    private Integer lunchIntake;

    private Integer waterCount;

    private String sleepStartTime;

    private Integer sleepDuration;

    private Integer sleepQuality;

    private Integer toiletCount;

    private String toiletAbnormal;

    private BigDecimal morningTemp;

    private BigDecimal afternoonTemp;

    private String healthSymptoms;

    private String teacherRemark;
}
