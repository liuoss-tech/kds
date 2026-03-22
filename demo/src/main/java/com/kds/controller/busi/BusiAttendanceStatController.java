package com.kds.controller.busi;

import com.kds.common.result.Result;
import com.kds.model.dto.AttendanceStatQueryDTO;
import com.kds.model.vo.*;
import com.kds.service.BusiAttendanceStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/busi/attendance/stat")
public class BusiAttendanceStatController {

    @Autowired
    private BusiAttendanceStatService busiAttendanceStatService;

    @GetMapping("/overview")
    public Result<AttendanceOverviewVO> getOverview(@Validated AttendanceStatQueryDTO queryDTO) {
        return Result.success(busiAttendanceStatService.getOverview(queryDTO));
    }

    @GetMapping("/class-rate")
    public Result<List<AttendanceClassRateVO>> getClassRateList(@Validated AttendanceStatQueryDTO queryDTO) {
        return Result.success(busiAttendanceStatService.getClassRateList(queryDTO));
    }

    @GetMapping("/leave")
    public Result<AttendanceLeaveStatVO> getLeaveStat(@Validated AttendanceStatQueryDTO queryDTO) {
        return Result.success(busiAttendanceStatService.getLeaveStat(queryDTO));
    }

    @GetMapping("/trend")
    public Result<List<AttendanceTrendVO>> getTrend(@Validated AttendanceStatQueryDTO queryDTO) {
        return Result.success(busiAttendanceStatService.getTrend(queryDTO));
    }
}