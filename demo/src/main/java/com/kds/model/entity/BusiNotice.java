package com.kds.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("busi_notice")
public class BusiNotice {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("notice_type")
    private Integer noticeType;

    @TableField("notice_level")
    private Integer noticeLevel;

    @TableField("target_scope")
    private Integer targetScope;

    @TableField("target_class_id")
    private Long targetClassId;

    @TableField("publisher_id")
    private Long publisherId;

    @TableField("publish_time")
    private LocalDateTime publishTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}