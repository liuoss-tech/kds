package com.kds.controller.busi;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kds.common.result.Result;
import com.kds.model.dto.ActivityFormDTO;
import com.kds.model.vo.ActivityDetailVO;
import com.kds.model.vo.ActivityVO;
import com.kds.model.vo.RegistrationVO;
import com.kds.service.BusiActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/busi/activity")
public class BusiActivityController {

    @Autowired
    private BusiActivityService busiActivityService;

    @GetMapping("/teacher/list")
    public Result<Page<ActivityVO>> getTeacherActivityPage(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(busiActivityService.getTeacherActivityPage(status, page, size));
    }

    @GetMapping("/parent/list")
    public Result<Page<ActivityVO>> getParentActivityPage(
            @RequestParam(defaultValue = "ongoing") String tab,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(busiActivityService.getParentActivityPage(tab, page, size));
    }

    @PostMapping
    public Result<?> publishActivity(@Validated @RequestBody ActivityFormDTO formDTO) {
        busiActivityService.publishActivity(formDTO);
        return Result.success("活动发布成功");
    }

    @GetMapping("/{id}")
    public Result<ActivityDetailVO> getActivityDetail(@PathVariable Long id) {
        return Result.success(busiActivityService.getActivityDetail(id));
    }

    @GetMapping("/{id}/registrations")
    public Result<List<RegistrationVO>> getActivityRegistrations(@PathVariable Long id) {
        return Result.success(busiActivityService.getActivityRegistrations(id));
    }

    @PostMapping("/registration")
    public Result<?> registerActivity(
            @RequestParam Long activityId,
            @RequestParam Long studentId,
            @RequestParam(required = false, defaultValue = "1") Integer participantCount,
            @RequestParam(required = false) String remark) {
        busiActivityService.registerActivity(activityId, studentId, participantCount, remark);
        return Result.success("报名成功");
    }

    @PutMapping("/registration/{id}/cancel")
    public Result<?> cancelRegistration(@PathVariable Long id) {
        busiActivityService.cancelRegistration(id);
        return Result.success("取消报名成功");
    }

    @GetMapping("/{id}/available-students")
    public Result<List<Long>> getAvailableStudentIds(@PathVariable Long id) {
        return Result.success(busiActivityService.getAvailableStudentIds(id));
    }
}