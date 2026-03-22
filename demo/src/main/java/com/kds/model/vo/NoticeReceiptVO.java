package com.kds.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeReceiptVO {

    private Long id;

    private Long noticeId;

    private Long studentId;

    private String studentName;

    private Long parentUserId;

    private String parentName;

    private String relation;

    private Integer isConfirmed;

    private LocalDateTime confirmTime;
}