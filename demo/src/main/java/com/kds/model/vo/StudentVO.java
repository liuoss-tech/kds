package com.kds.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 幼儿列表 VO
 */
@Data
public class StudentVO {

    private Long id;

    private String studentName;

    private Integer gender;

    private LocalDate birthday;

    private Long classId;

    private String className;

    private Integer grade;

    private String avatar;

    private Integer status;

    private List<ParentSimpleVO> parents;

    private LocalDateTime createTime;

    @Data
    public static class ParentSimpleVO {
        private Long parentUserId;
        private String parentName;
        private String relation;
        private Integer isPrimary;
    }
}
