package com.kds.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.dto.LifeRecordBatchSaveDTO;
import com.kds.model.dto.LifeRecordFormDTO;
import com.kds.model.dto.LifeRecordQueryDTO;
import com.kds.model.vo.LifeRecordVO;
import com.kds.model.vo.ParentLifeRecordVO;

import java.time.LocalDate;

public interface BusiLifeRecordService extends IService<com.kds.model.entity.BusiLifeRecord> {
    Page<LifeRecordVO> getLifeRecordPage(LifeRecordQueryDTO queryDTO);
    void saveRecord(LifeRecordFormDTO formDTO);
    void batchSave(LifeRecordBatchSaveDTO batchDTO);
    ParentLifeRecordVO getParentLifeRecord(Long studentId, LocalDate date);
}