package com.km.taskflow.module.system.controller;

import com.km.taskflow.common.exception.BusinessException;
import com.km.taskflow.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zzy
 * @description
 * @create 2026-05-07 13:52:04
 */
@Tag(name = "测试模块")
@Validated
@RestController
public class TestController {
    @Operation(summary = "测试成功响应", description = "直接返回一段成功的字符串")
    @GetMapping("/test/success")
    public Result<String> success() {
        return Result.success("TaskFlow 启动成功");
    }

    @Operation(summary = "测试业务异常", description = "模拟抛出一个 BusinessException 并被全局拦截")
    @GetMapping("/test/business-error")
    public Result<Void> businessError() {
        throw new BusinessException("这是一个业务异常");
    }
    
    @Operation(summary = "测试单参数校验", description = "测试 @NotBlank 注解是否生效")
    @GetMapping("/test/param-error")
    public Result<String> paramError(
            @Parameter(description = "测试名称", required = true)
            @NotBlank(message = "名称不能为空") @RequestParam String name) {
        return Result.success(name);
    }
}
