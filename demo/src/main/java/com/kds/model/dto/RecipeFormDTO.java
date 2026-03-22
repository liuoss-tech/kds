package com.kds.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class RecipeFormDTO {

    private Long id;

    @NotNull(message = "班级ID不能为空")
    private Long classId;

    @NotNull(message = "食谱日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetDate;

    @NotBlank(message = "早餐不能为空")
    private String breakfast;

    private String morningSnack;

    @NotBlank(message = "午餐不能为空")
    private String lunch;

    private String afternoonSnack;

    private String dinner;
}