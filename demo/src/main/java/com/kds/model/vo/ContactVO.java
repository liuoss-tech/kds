package com.kds.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ContactVO {

    private Long classId;

    private String className;

    private Long studentId;

    private String studentName;

    private List<ParentVO> parents;

    private List<TeacherVO> teachers;

    @Data
    public static class ParentVO {
        private Long userId;
        private String realName;
        private String relation;
    }

    @Data
    public static class TeacherVO {
        private Long userId;
        private String realName;
        private Boolean isHeadTeacher;
    }
}
