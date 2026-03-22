package com.kds.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 幼儿档案实体类
 * 对应数据库表：busi_student
 */
@Data
@TableName("busi_student")
public class BusiStudent {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("class_id")
    private Long classId;

    @TableField("student_name")
    private String studentName;

    private Integer gender;

    private LocalDate birthday;

    @TableField("id_card")
    private String idCard;

    @TableField("admission_date")
    private LocalDate admissionDate;

    private Integer status;

    @TableField("avatar")
    private String avatar;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
