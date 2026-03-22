package com.kds.model.vo;

import lombok.Data;

/**
 * 教师简单视图对象
 * 用于班级列表中展示教师信息
 */
@Data
public class TeacherSimpleVO {

    /**
     * 教师ID
     */
    private Long id;

    /**
     * 教师姓名
     */
    private String realName;

    /**
     * 是否为班主任：0-否 1-是
     */
    private Integer isHeadTeacher;
}
