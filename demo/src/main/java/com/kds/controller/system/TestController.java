package com.kds.controller.system;

import com.kds.common.exception.BusinessException;
import com.kds.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器 - 用于验证全局异常和鉴权是否生效
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 测试接口 - 需要登录才能访问
     * 如果未带 Token 请求，会被 SaTokenConfig 拦截返回 401
     */
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("登录成功才能看到我！");
    }

    /**
     * 测试全局异常处理 - 业务异常
     * 主动抛出一个业务异常，验证 GlobalExceptionHandler 是否正常工作
     * 期望返回：{"code": 500, "message": "这是一个测试的业务报错", "data": null}
     */
    @GetMapping("/error")
    public Result<?> error() {
        throw new BusinessException("这是一个测试的业务报错");
    }

    /**
     * 测试全局异常处理 - 参数校验异常
     */
    @GetMapping("/param-error")
    public Result<?> paramError() {
        throw new BusinessException(400, "参数错误测试");
    }
}
