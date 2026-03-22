package com.kds.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kds.model.dto.AttendanceFormDTO;
import com.kds.model.dto.AttendanceQueryDTO;
import com.kds.model.vo.AttendanceVO;

import java.time.LocalDate;
import java.util.List;

public interface BusiAttendanceRecordService extends IService<com.kds.model.entity.BusiAttendanceRecord> {

    Page<AttendanceVO> getAttendancePage(AttendanceQueryDTO queryDTO);

    List<AttendanceVO> getParentAttendanceList(Long studentId, LocalDate date);

    void signIn(AttendanceFormDTO formDTO);

    void signOut(AttendanceFormDTO formDTO);
}
