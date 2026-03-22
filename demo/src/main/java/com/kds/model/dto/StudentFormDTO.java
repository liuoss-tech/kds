package com.kds.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 幼儿表单 DTO（新增/编辑）
 */
@Data
public class StudentFormDTO {

    private Long id;

    @NotBlank(message = "幼儿姓名不能为空")
    private String studentName;

    @NotNull(message = "请选择性别")
    private Integer gender;

    @NotNull(message = "出生日期不能为空")
    private LocalDate birthday;

    @NotNull(message = "请选择所属班级")
    private Long classId;

    private LocalDate admissionDate;

    private String idCard;

    private String avatar;

    private Integer status;

    private List<StudentParentDTO> parents;

    @Data
    public static class StudentParentDTO {
        private Long parentUserId;
        private String relation;
        private Integer isPrimary;
    }
}
