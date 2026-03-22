package com.kds.model.vo;

import lombok.Data;

/**
 * 家长关联的幼儿信息 VO
 */
@Data
public class ChildInfoVO {

    private Long studentId;

    private String studentName;

    private Long classId;

    private String className;
}