package com.kds.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityVO {

    private Long id;

    private String title;

    private Integer activityType;

    private String activityTypeText;

    private LocalDateTime activityTime;

    private LocalDateTime deadlineTime;

    private String location;

    private Integer targetScope;

    private Long targetClassId;

    private String targetClassName;

    private Integer status;

    private String statusText;

    private Integer registrationCount;

    private Boolean isRegistered;

    private LocalDateTime createTime;
}