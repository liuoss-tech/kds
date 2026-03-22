package com.kds.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class AttendanceFormDTO {

    @NotEmpty(message = "幼儿ID列表不能为空")
    private List<Long> studentIds;

    @NotNull(message = "班级ID不能为空")
    private Long classId;

    @NotNull(message = "考勤日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}
