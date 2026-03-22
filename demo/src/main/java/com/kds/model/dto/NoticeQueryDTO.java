package com.kds.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NoticeQueryDTO {

    private String title;

    private Integer noticeType;

    private Integer noticeLevel;

    private Integer confirmStatus;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}