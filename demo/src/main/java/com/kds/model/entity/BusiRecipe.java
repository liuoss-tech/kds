package com.kds.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("busi_recipe")
public class BusiRecipe {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("class_id")
    private Long classId;

    @TableField("target_date")
    private LocalDate targetDate;

    @TableField("breakfast")
    private String breakfast;

    @TableField("morning_snack")
    private String morningSnack;

    @TableField("lunch")
    private String lunch;

    @TableField("afternoon_snack")
    private String afternoonSnack;

    @TableField("dinner")
    private String dinner;

    @TableField("publisher_id")
    private Long publisherId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}