package com.kds.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 班级视图对象
 * 用于前端展示，包含教师列表
 */
@Data
public class ClassVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 年级：1-托班 2-小班 3-中班 4-大班
     */
    private Integer grade;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 班级教师列表（用于展示已分配的教师）
     */
    private List<TeacherSimpleVO> teachers;
}
