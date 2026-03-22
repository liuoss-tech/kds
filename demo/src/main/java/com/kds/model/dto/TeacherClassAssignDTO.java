package com.kds.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 教师班级分配数据传输对象
 */
@Data
public class TeacherClassAssignDTO {

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 教师ID列表
     */
    private List<Long> teacherIds;

    /**
     * 是否设置为班主任：0-否 1-是
     */
    private Integer isHeadTeacher;
}
