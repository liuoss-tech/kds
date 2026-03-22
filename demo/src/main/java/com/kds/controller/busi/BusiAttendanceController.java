package com.kds.controller.busi;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kds.common.result.Result;
import com.kds.model.dto.AttendanceFormDTO;
import com.kds.model.dto.AttendanceQueryDTO;
import com.kds.model.vo.AttendanceVO;
import com.kds.service.BusiAttendanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/busi/attendance")
public class BusiAttendanceController {

    @Autowired
    private BusiAttendanceRecordService busiAttendanceRecordService;

    @GetMapping("/page")
    public Result<Page<AttendanceVO>> getAttendancePage(AttendanceQueryDTO queryDTO) {
        return Result.success(busiAttendanceRecordService.getAttendancePage(queryDTO));
    }

    @GetMapping("/parent/list")
    public Result<List<AttendanceVO>> getParentAttendanceList(
            @RequestParam Long studentId,
            @RequestParam(required = false) String date) {
        LocalDate recordDate = date != null ? LocalDate.parse(date) : null;
        return Result.success(busiAttendanceRecordService.getParentAttendanceList(studentId, recordDate));
    }

    @PostMapping("/sign-in")
    public Result<?> signIn(@Validated @RequestBody AttendanceFormDTO formDTO) {
        busiAttendanceRecordService.signIn(formDTO);
        return Result.success("签到成功");
    }

    @PostMapping("/sign-out")
    public Result<?> signOut(@Validated @RequestBody AttendanceFormDTO formDTO) {
        busiAttendanceRecordService.signOut(formDTO);
        return Result.success("签退成功");
    }
}
