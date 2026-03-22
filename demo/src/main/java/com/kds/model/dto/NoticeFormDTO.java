package com.kds.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NoticeFormDTO {

    private Long id;

    @NotBlank(message = "请填写通知标题")
    private String title;

    @NotBlank(message = "请填写通知内容")
    private String content;

    @NotNull(message = "请选择通知类型")
    private Integer noticeType;

    @NotNull(message = "请选择通知级别")
    private Integer noticeLevel;

    @NotNull(message = "请选择发送范围")
    private Integer targetScope;

    private Long targetClassId;
}