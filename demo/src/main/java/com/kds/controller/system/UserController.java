package com.kds.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kds.common.result.Result;
import com.kds.model.dto.UserFormDTO;
import com.kds.model.dto.UserQueryDTO;
import com.kds.model.entity.SysUser;
import com.kds.model.vo.UserVO;
import com.kds.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/page")
    public Result<Page<UserVO>> getUserPage(UserQueryDTO queryDTO) {
        Page<UserVO> page = sysUserService.getUserPage(queryDTO);
        return Result.success(page);
    }

    @PostMapping("/add")
    public Result<?> addUser(@Validated @RequestBody UserFormDTO formDTO) {
        sysUserService.addUser(formDTO);
        return Result.success("新增成功");
    }

    @PostMapping("/update")
    public Result<?> updateUser(@Validated @RequestBody UserFormDTO formDTO) {
        sysUserService.updateUser(formDTO);
        return Result.success("修改成功");
    }

    @PostMapping("/status")
    public Result<?> updateStatus(@RequestBody SysUser user) {
        sysUserService.updateById(user);
        return Result.success("状态更新成功");
    }

    @PostMapping("/delete/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success("删除成功");
    }

    /**
     * 获取教师列表
     * 用于前端下拉框选择班主任
     * @return 教师列表
     */
    @GetMapping("/teacher-list")
    public Result<List<SysUser>> getTeacherList() {
        // 查询 user_type = 1 (教职工) 并且 status = 1 (正常) 且未删除的用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserType, 1)
               .eq(SysUser::getStatus, 1)
               .eq(SysUser::getIsDeleted, 0)
               .select(SysUser::getId, SysUser::getRealName);

        List<SysUser> teachers = sysUserService.list(wrapper);
        return Result.success(teachers);
    }
}
