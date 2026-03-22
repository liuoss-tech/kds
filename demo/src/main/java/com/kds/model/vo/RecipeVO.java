package com.kds.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RecipeVO {

    private Long id;

    private Long classId;

    private String className;

    private String studentName;

    private LocalDate targetDate;

    private String breakfast;

    private String morningSnack;

    private String lunch;

    private String afternoonSnack;

    private String dinner;

    private Long publisherId;

    private String publisherName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}