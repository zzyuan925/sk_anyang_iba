package com.sk.iba.module.device.controller;

import com.sk.iba.common.log.OperationLog;
import com.sk.iba.common.log.OperationType;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.common.result.Result;
import com.sk.iba.module.device.dto.AlgorithmPackageCreateDTO;
import com.sk.iba.module.device.dto.AlgorithmPackageQueryDTO;
import com.sk.iba.module.device.dto.AlgorithmPackageUpdateDTO;
import com.sk.iba.module.device.service.AlgorithmPackageService;
import com.sk.iba.module.device.vo.AlgorithmPackageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 算法包管理
 *
 * @author zzy
 */
@Tag(name = "算法包管理模块", description = "算法包上传、删除、修改、查询")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/device/algorithm-package")
public class AlgorithmPackageController {

    private final AlgorithmPackageService algorithmPackageService;

    @OperationLog(module = "算法包管理", name = "分页查询算法包", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "分页查询算法包")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('device:algorithm-package:list')")
    public Result<PageResult<AlgorithmPackageVO>> page(@ParameterObject @Validated AlgorithmPackageQueryDTO queryDTO) {
        return Result.success(algorithmPackageService.pageAlgorithmPackages(queryDTO));
    }

    @OperationLog(module = "算法包管理", name = "获取算法包详情", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "获取算法包详情")
    @Parameter(name = "id", description = "算法包ID", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('device:algorithm-package:detail')")
    public Result<AlgorithmPackageVO> getById(@PathVariable @NotNull(message = "算法包ID不能为空") Long id) {
        return Result.success(algorithmPackageService.getAlgorithmPackageById(id));
    }

    @OperationLog(module = "算法包管理", name = "上传算法包", type = OperationType.CREATE, recordResult = false)
    @Operation(summary = "上传算法包")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('device:algorithm-package:create')")
    public Result<Long> create(@ModelAttribute @Valid AlgorithmPackageCreateDTO createDTO) {
        return Result.success(algorithmPackageService.createAlgorithmPackage(createDTO));
    }

    @OperationLog(module = "算法包管理", name = "修改算法包", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "修改算法包", description = "只能修改描述、启动环境、启动文件名和权重路径")
    @PutMapping
    @PreAuthorize("hasAuthority('device:algorithm-package:update')")
    public Result<Void> update(@RequestBody @Valid AlgorithmPackageUpdateDTO updateDTO) {
        algorithmPackageService.updateAlgorithmPackage(updateDTO);
        return Result.success();
    }

    @OperationLog(module = "算法包管理", name = "删除算法包", type = OperationType.DELETE, recordResult = false)
    @Operation(summary = "逻辑删除算法包")
    @Parameter(name = "id", description = "算法包ID", required = true, example = "1")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('device:algorithm-package:delete')")
    public Result<Void> delete(@PathVariable @NotNull(message = "算法包ID不能为空") Long id) {
        algorithmPackageService.deleteAlgorithmPackage(id);
        return Result.success();
    }
}