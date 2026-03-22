package com.kds.model.dto;

import lombok.Data;

/**
 * 班级查询数据传输对象
 * 用于分页查询条件
 */
@Data
public class ClassQueryDTO {

    /**
     * 当前页码
     */
    private Integer page = 1;

    /**
     * 每页条数
     */
    private Integer size = 10;

    /**
     * 班级名称（模糊搜索）
     */
    private String className;

    /**
     * 年级：1-托班 2-小班 3-中班 4-大班
     */
    private Integer grade;
}
