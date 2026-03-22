package com.kds.controller.busi;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kds.common.result.Result;
import com.kds.model.dto.ClassFormDTO;
import com.kds.model.dto.ClassQueryDTO;
import com.kds.model.vo.ClassVO;
import com.kds.service.BusiClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 班级管理 Controller
 */
@RestController
@RequestMapping("/api/busi/class")
public class BusiClassController {

    @Autowired
    private BusiClassService busiClassService;

    /**
     * 分页查询班级列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<Page<ClassVO>> getClassPage(ClassQueryDTO queryDTO) {
        return Result.success(busiClassService.getClassPage(queryDTO));
    }

    /**
     * 新增班级
     * @param formDTO 班级表单数据
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<?> addClass(@Validated @RequestBody ClassFormDTO formDTO) {
        busiClassService.addClass(formDTO);
        return Result.success("新增班级成功");
    }

    /**
     * 修改班级
     * @param formDTO 班级表单数据
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<?> updateClass(@Validated @RequestBody ClassFormDTO formDTO) {
        busiClassService.updateClass(formDTO);
        return Result.success("修改班级成功");
    }

    /**
     * 删除班级
     * @param id 班级ID
     * @return 操作结果
     */
    @PostMapping("/delete/{id}")
    public Result<?> deleteClass(@PathVariable Long id) {
        busiClassService.deleteClass(id);
        return Result.success("删除班级成功");
    }
}
