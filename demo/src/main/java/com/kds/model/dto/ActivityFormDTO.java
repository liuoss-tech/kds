package com.kds.model.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ActivityFormDTO {

    private Long id;

    @NotBlank(message = "活动名称不能为空")
    private String title;

    @NotNull(message = "活动类型不能为空")
    private Integer activityType;

    @NotNull(message = "活动时间不能为空")
    private LocalDateTime activityTime;

    @NotNull(message = "报名截止时间不能为空")
    private LocalDateTime deadlineTime;

    @NotBlank(message = "活动地点不能为空")
    private String location;

    @NotBlank(message = "活动详情不能为空")
    private String content;

    @NotNull(message = "面向范围不能为空")
    private Integer targetScope;

    private Long targetClassId;
}