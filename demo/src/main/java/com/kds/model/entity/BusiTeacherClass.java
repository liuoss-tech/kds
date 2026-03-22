package com.kds.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教师任教班级分配实体类
 * 对应数据库表：busi_teacher_class
 */
@Data
@TableName("busi_teacher_class")
public class BusiTeacherClass {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 教师用户ID
     */
    @TableField("teacher_user_id")
    private Long teacherUserId;

    /**
     * 班级ID
     */
    @TableField("class_id")
    private Long classId;

    /**
     * 是否为该班班主任：0-否 1-是
     */
    @TableField("is_head_teacher")
    private Integer isHeadTeacher;

    /**
     * 分配时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
