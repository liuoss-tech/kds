package com.kds.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class LifeRecordQueryDTO {

    @NotNull(message = "班级ID不能为空")
    private Long classId;

    @NotNull(message = "日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Long page = 1L;
    private Long size = 100L;
}
