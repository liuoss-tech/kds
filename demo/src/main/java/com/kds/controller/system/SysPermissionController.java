package com.kds.controller.system;

import cn.dev33.satoken.stp.StpUtil;
import com.kds.common.result.Result;
import com.kds.model.entity.SysPermission;
import com.kds.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/permission")
public class SysPermissionController {

    @Autowired
    private SysPermissionService sysPermissionService;

    @GetMapping("/my-menus")
    public Result<List<SysPermission>> getMyMenus() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<SysPermission> menus = sysPermissionService.getMyMenus(userId);
        return Result.success(menus);
    }
}
