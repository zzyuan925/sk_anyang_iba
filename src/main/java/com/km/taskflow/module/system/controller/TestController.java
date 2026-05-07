package com.km.taskflow.module.system.controller;

import com.km.taskflow.common.exception.BusinessException;
import com.km.taskflow.common.result.Result;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zzy
 * @description
 * @create 2026-05-07 13:52:04
 */
@Validated
@RestController
public class TestController {
    @GetMapping("/test/success")
    public Result<String> success() {
        return Result.success("TaskFlow 启动成功");
    }

    @GetMapping("/test/business-error")
    public Result<Void> businessError() {
        throw new BusinessException("这是一个业务异常");
    }

    @GetMapping("/test/param-error")
    public Result<String> paramError(@NotBlank(message = "名称不能为空") String name) {
        return Result.success(name);
    }
}
