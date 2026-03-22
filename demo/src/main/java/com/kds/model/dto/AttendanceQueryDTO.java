package com.kds.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class AttendanceQueryDTO {

    private Long classId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Integer page = 1;

    private Integer size = 10;
}
