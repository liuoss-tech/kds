package com.kds.service;

import com.kds.model.dto.AttendanceStatQueryDTO;
import com.kds.model.vo.*;

import java.util.List;

public interface BusiAttendanceStatService {

    AttendanceOverviewVO getOverview(AttendanceStatQueryDTO queryDTO);

    List<AttendanceClassRateVO> getClassRateList(AttendanceStatQueryDTO queryDTO);

    AttendanceLeaveStatVO getLeaveStat(AttendanceStatQueryDTO queryDTO);

    List<AttendanceTrendVO> getTrend(AttendanceStatQueryDTO queryDTO);
}