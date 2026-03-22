package com.kds.controller.busi;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kds.common.result.Result;
import com.kds.model.dto.LeaveApproveDTO;
import com.kds.model.dto.LeaveFormDTO;
import com.kds.model.dto.LeaveQueryDTO;
import com.kds.model.vo.LeaveVO;
import com.kds.model.vo.StudentSimpleVO;
import com.kds.service.BusiLeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/busi/leave")
public class BusiLeaveApplicationController {

    @Autowired
    private BusiLeaveApplicationService busiLeaveApplicationService;

    @PostMapping("/add")
    public Result<?> addLeave(@Validated @RequestBody LeaveFormDTO formDTO) {
        busiLeaveApplicationService.addLeave(formDTO);
        return Result.success("请假申请已提交");
    }

    @GetMapping("/page")
    public Result<Page<LeaveVO>> getParentLeavePage(LeaveQueryDTO queryDTO) {
        return Result.success(busiLeaveApplicationService.getParentLeavePage(queryDTO));
    }

    @GetMapping("/teacher-page")
    public Result<Page<LeaveVO>> getTeacherLeavePage(LeaveQueryDTO queryDTO) {
        return Result.success(busiLeaveApplicationService.getTeacherLeavePage(queryDTO));
    }

    @GetMapping("/admin-page")
    public Result<Page<LeaveVO>> getAdminLeavePage(LeaveQueryDTO queryDTO) {
        return Result.success(busiLeaveApplicationService.getAdminLeavePage(queryDTO));
    }

    @PostMapping("/approve")
    public Result<?> approveLeave(@Validated @RequestBody LeaveApproveDTO approveDTO) {
        busiLeaveApplicationService.approveLeave(approveDTO);
        return Result.success("审批成功");
    }

    @GetMapping("/students")
    public Result<List<StudentSimpleVO>> getMyStudents() {
        return Result.success(busiLeaveApplicationService.getMyStudents());
    }
}
