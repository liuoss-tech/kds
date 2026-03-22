package com.kds.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RegistrationVO {

    private Long id;

    private Long activityId;

    private Long studentId;

    private String studentName;

    private Long parentUserId;

    private String parentRelation;

    private Integer participantCount;

    private String remark;

    private Integer status;

    private LocalDateTime createTime;
}