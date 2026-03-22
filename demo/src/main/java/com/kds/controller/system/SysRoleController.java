package com.kds.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kds.common.result.Result;
import com.kds.model.dto.RoleAssignDTO;
import com.kds.model.dto.RoleFormDTO;
import com.kds.model.dto.RoleQueryDTO;
import com.kds.model.entity.SysRole;
import com.kds.model.vo.PermissionTreeVO;
import com.kds.service.SysPermissionService;
import com.kds.service.SysRolePermissionService;
import com.kds.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysPermissionService sysPermissionService;

    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    @GetMapping("/list")
    public Result<List<SysRole>> getRoleList() {
        return Result.success(sysRoleService.list());
    }

    @GetMapping("/page")
    public Result<Page<SysRole>> getRolePage(RoleQueryDTO queryDTO) {
        return Result.success(sysRoleService.getRolePage(queryDTO));
    }

    @PostMapping("/add")
    public Result<?> addRole(@Validated @RequestBody RoleFormDTO formDTO) {
        sysRoleService.addRole(formDTO);
        return Result.success("新增角色成功");
    }

    @PostMapping("/update")
    public Result<?> updateRole(@Validated @RequestBody RoleFormDTO formDTO) {
        sysRoleService.updateRole(formDTO);
        return Result.success("修改角色成功");
    }

    @PostMapping("/delete/{id}")
    public Result<?> deleteRole(@PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Result.success("删除角色成功");
    }

    @GetMapping("/permission/tree")
    public Result<List<PermissionTreeVO>> getPermissionTree() {
        return Result.success(sysPermissionService.getPermissionTree());
    }

    @GetMapping("/permission/own/{roleId}")
    public Result<List<Long>> getRoleOwnPermissionIds(@PathVariable Long roleId) {
        return Result.success(sysRolePermissionService.getPermissionIdsByRoleId(roleId));
    }

    @PostMapping("/permission/assign")
    public Result<?> assignPermissions(@RequestBody RoleAssignDTO dto) {
        sysRolePermissionService.assignPermissions(dto.getRoleId(), dto.getPermissionIds());
        return Result.success("权限分配成功！");
    }
}
