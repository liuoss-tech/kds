package com.kds.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("busi_student_parent")
public class BusiStudentParent {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("student_id")
    private Long studentId;

    @TableField("parent_user_id")
    private Long parentUserId;

    private String relation;

    @TableField("is_primary")
    private Integer isPrimary;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
