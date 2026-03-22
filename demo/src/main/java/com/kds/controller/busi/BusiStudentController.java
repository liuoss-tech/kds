package com.kds.controller.busi;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kds.common.context.LoginHelper;
import com.kds.common.result.Result;
import com.kds.model.dto.StudentFormDTO;
import com.kds.model.dto.StudentQueryDTO;
import com.kds.model.vo.ChildInfoVO;
import com.kds.model.vo.StudentDetailVO;
import com.kds.model.vo.StudentVO;
import com.kds.service.BusiStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 幼儿管理 Controller
 */
@RestController
@RequestMapping("/api/busi/student")
public class BusiStudentController {

    @Autowired
    private BusiStudentService busiStudentService;

    @Autowired
    private LoginHelper loginHelper;

    /**
     * 分页查询幼儿列表
     */
    @GetMapping("/page")
    public Result<Page<StudentVO>> getStudentPage(StudentQueryDTO queryDTO) {
        return Result.success(busiStudentService.getStudentPage(queryDTO));
    }

    /**
     * 获取幼儿详情
     */
    @GetMapping("/{id}")
    public Result<StudentDetailVO> getStudentDetail(@PathVariable Long id) {
        return Result.success(busiStudentService.getStudentDetail(id));
    }

    /**
     * 新增幼儿
     */
    @PostMapping("/add")
    public Result<?> addStudent(@Validated @RequestBody StudentFormDTO formDTO) {
        busiStudentService.addStudent(formDTO);
        return Result.success("新增幼儿成功");
    }

    /**
     * 修改幼儿
     */
    @PostMapping("/update")
    public Result<?> updateStudent(@Validated @RequestBody StudentFormDTO formDTO) {
        busiStudentService.updateStudent(formDTO);
        return Result.success("修改幼儿成功");
    }

    /**
     * 删除幼儿
     */
    @PostMapping("/delete/{id}")
    public Result<?> deleteStudent(@PathVariable Long id) {
        busiStudentService.deleteStudent(id);
        return Result.success("删除幼儿成功");
    }

    /**
     * 获取家长列表（用于选择）
     */
    @GetMapping("/parent-list")
    public Result<List<BusiStudentService.ParentOptionVO>> getParentList() {
        return Result.success(busiStudentService.getParentList());
    }

    /**
     * 获取班级列表（用于下拉选择）
     */
    @GetMapping("/class-list")
    public Result<List<BusiStudentService.ClassOptionVO>> getClassList() {
        return Result.success(busiStudentService.getClassList());
    }

    /**
     * 获取当前家长绑定的幼儿列表
     */
    @GetMapping("/parent-children")
    public Result<List<ChildInfoVO>> getParentChildren() {
        Long parentUserId = loginHelper.getUserId();
        return Result.success(busiStudentService.getParentChildren(parentUserId));
    }
}
