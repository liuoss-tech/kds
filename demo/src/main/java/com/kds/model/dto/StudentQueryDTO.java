package com.kds.model.dto;

import lombok.Data;

/**
 * 幼儿查询条件 DTO
 */
@Data
public class StudentQueryDTO {

    private Integer page = 1;

    private Integer size = 10;

    private String studentName;

    private Long classId;

    private Integer status = 1;
}
