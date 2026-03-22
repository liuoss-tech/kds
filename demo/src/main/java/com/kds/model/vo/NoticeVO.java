package com.kds.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeVO {

    private Long id;

    private String title;

    private Integer noticeType;

    private String noticeTypeText;

    private Integer noticeLevel;

    private String noticeLevelText;

    private Integer targetScope;

    private String targetScopeText;

    private Long targetClassId;

    private String targetClassName;

    private Long publisherId;

    private String publisherName;

    private LocalDateTime publishTime;

    private LocalDateTime createTime;

    private Integer confirmStatus;
}