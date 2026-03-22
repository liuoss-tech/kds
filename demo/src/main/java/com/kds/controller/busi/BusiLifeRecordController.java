package com.kds.controller.busi;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kds.common.result.Result;
import com.kds.model.dto.LifeRecordBatchSaveDTO;
import com.kds.model.dto.LifeRecordFormDTO;
import com.kds.model.dto.LifeRecordQueryDTO;
import com.kds.model.vo.LifeRecordVO;
import com.kds.model.vo.ParentLifeRecordVO;
import com.kds.service.BusiLifeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/busi/life-record")
public class BusiLifeRecordController {

    @Autowired
    private BusiLifeRecordService busiLifeRecordService;

    @GetMapping("/page")
    public Result<Page<LifeRecordVO>> getLifeRecordPage(LifeRecordQueryDTO queryDTO) {
        return Result.success(busiLifeRecordService.getLifeRecordPage(queryDTO));
    }

    @PostMapping("/save")
    public Result<?> saveLifeRecord(@Validated @RequestBody LifeRecordFormDTO formDTO) {
        busiLifeRecordService.saveRecord(formDTO);
        return Result.success("生活记录保存成功");
    }

    @PostMapping("/batch-save")
    public Result<?> batchSaveLifeRecord(@Validated @RequestBody LifeRecordBatchSaveDTO batchDTO) {
        busiLifeRecordService.batchSave(batchDTO);
        return Result.success("批量保存成功");
    }

    @GetMapping("/parent/page")
    public Result<ParentLifeRecordVO> getParentLifeRecord(
            @RequestParam Long studentId,
            @RequestParam(required = false) String date) {
        LocalDate recordDate = date != null ? LocalDate.parse(date) : null;
        return Result.success(busiLifeRecordService.getParentLifeRecord(studentId, recordDate));
    }
}
