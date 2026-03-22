package com.kds.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("busi_notice_receipt")
public class BusiNoticeReceipt {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("notice_id")
    private Long noticeId;

    @TableField("student_id")
    private Long studentId;

    @TableField("parent_user_id")
    private Long parentUserId;

    @TableField("is_confirmed")
    private Integer isConfirmed;

    @TableField("confirm_time")
    private LocalDateTime confirmTime;
}