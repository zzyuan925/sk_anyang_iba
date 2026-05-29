package com.sk.iba.module.device.controller;

import com.sk.iba.common.log.OperationLog;
import com.sk.iba.common.log.OperationType;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.common.result.Result;
import com.sk.iba.module.device.dto.AlgorithmRuntimeDeployDTO;
import com.sk.iba.module.device.dto.AlgorithmRuntimeQueryDTO;
import com.sk.iba.module.device.service.AlgorithmRuntimeService;
import com.sk.iba.module.device.vo.AlgorithmRuntimeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 算法运行管理
 *
 * @author zzy
 */
@Tag(name = "算法运行模块", description = "算法包部署、运行列表、启动、停止")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/device/algorithm-runtime")
public class AlgorithmRuntimeController {

    private final AlgorithmRuntimeService algorithmRuntimeService;

    @OperationLog(module = "算法运行管理", name = "分页查询算法运行", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "分页查询算法运行")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('device:algorithm-runtime:list')")
    public Result<PageResult<AlgorithmRuntimeVO>> page(@ParameterObject @Validated AlgorithmRuntimeQueryDTO queryDTO) {
        return Result.success(algorithmRuntimeService.pageAlgorithmRuntimes(queryDTO));
    }

    @OperationLog(module = "算法运行管理", name = "部署算法包", type = OperationType.CREATE, recordResult = false)
    @Operation(summary = "部署算法包")
    @PostMapping("/deploy")
    @PreAuthorize("hasAuthority('device:algorithm-runtime:deploy')")
    public Result<Long> deploy(@RequestBody @Valid AlgorithmRuntimeDeployDTO deployDTO) {
        return Result.success(algorithmRuntimeService.deployAlgorithmPackage(deployDTO));
    }

    @OperationLog(module = "算法运行管理", name = "启动算法", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "启动算法")
    @Parameter(name = "id", description = "算法运行ID", required = true, example = "1")
    @PostMapping("/{id}/start")
    @PreAuthorize("hasAuthority('device:algorithm-runtime:start')")
    public Result<Void> start(@PathVariable @NotNull(message = "算法运行ID不能为空") Long id) {
        algorithmRuntimeService.startAlgorithm(id);
        return Result.success();
    }

    @OperationLog(module = "算法运行管理", name = "停止算法", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "停止算法")
    @Parameter(name = "id", description = "算法运行ID", required = true, example = "1")
    @PostMapping("/{id}/stop")
    @PreAuthorize("hasAuthority('device:algorithm-runtime:stop')")
    public Result<Void> stop(@PathVariable @NotNull(message = "算法运行ID不能为空") Long id) {
        algorithmRuntimeService.stopAlgorithm(id);
        return Result.success();
    }
}