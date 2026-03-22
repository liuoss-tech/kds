package com.kds.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 幼儿详情 VO
 */
@Data
public class StudentDetailVO {

    private Long id;

    private String studentName;

    private Integer gender;

    private LocalDate birthday;

    private String idCard;

    private Long classId;

    private String className;

    private Integer grade;

    private LocalDate admissionDate;

    private String avatar;

    private Integer status;

    private List<ParentVO> parents;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Data
    public static class ParentVO {
        private Long id;
        private Long parentUserId;
        private String parentName;
        private String parentPhone;
        private String relation;
        private Integer isPrimary;
    }
}
