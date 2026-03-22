package com.kds.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityDetailVO {

    private Long id;

    private String title;

    private Integer activityType;

    private String activityTypeText;

    private LocalDateTime activityTime;

    private LocalDateTime deadlineTime;

    private String location;

    private String content;

    private Integer targetScope;

    private Long targetClassId;

    private String targetClassName;

    private Integer status;

    private String statusText;

    private Long publisherId;

    private String publisherName;

    private LocalDateTime createTime;

    private List<RegistrationVO> myRegistrations;
}