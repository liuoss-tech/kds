package com.kds.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 班级表单数据传输对象
 * 用于新增和修改班级
 */
@Data
public class ClassFormDTO {

    /**
     * 班级ID（修改时需要传值）
     */
    private Long id;

    /**
     * 班级名称
     */
    @NotBlank(message = "班级名称不能为空")
    private String className;

    /**
     * 年级：1-托班 2-小班 3-中班 4-大班
     */
    @NotNull(message = "年级不能为空")
    private Integer grade;
}
