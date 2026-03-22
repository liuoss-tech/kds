package com.kds.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
public class LifeRecordBatchSaveDTO {

    @NotNull(message = "班级ID不能为空")
    private Long classId;

    @NotNull(message = "记录日期不能为空")
    private String recordDate;

    @NotNull(message = "记录列表不能为空")
    private List<Map<String, Object>> records;
}
