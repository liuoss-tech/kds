package com.kds.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("busi_activity")
public class BusiActivity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("activity_type")
    private Integer activityType;

    @TableField("activity_time")
    private LocalDateTime activityTime;

    @TableField("deadline_time")
    private LocalDateTime deadlineTime;

    @TableField("location")
    private String location;

    @TableField("target_scope")
    private Integer targetScope;

    @TableField("target_class_id")
    private Long targetClassId;

    @TableField("publisher_id")
    private Long publisherId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}