package com.kds.model.vo;

import lombok.Data;

/**
 * 家长绑定的幼儿简单 VO
 * 用于家长为幼儿请假时选择幼儿
 */
@Data
public class StudentSimpleVO {

    private Long id;

    private String studentName;

    private Long classId;

    private String className;
}
