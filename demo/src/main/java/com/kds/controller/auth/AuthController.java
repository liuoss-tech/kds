package com.kds.controller.auth;

import com.kds.common.result.Result;
import com.kds.model.dto.LoginDTO;
import com.kds.model.vo.CurrentUserVO;
import com.kds.model.vo.LoginVO;
import com.kds.service.SysUserService;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = sysUserService.login(loginDTO);
        return Result.success(loginVO);
    }

    @GetMapping("/info")
    public Result<CurrentUserVO> getCurrentUserInfo() {
        CurrentUserVO userInfo = sysUserService.getCurrentUserInfo();
        return Result.success(userInfo);
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        StpUtil.logout();
        return Result.success("退出成功");
    }
}
