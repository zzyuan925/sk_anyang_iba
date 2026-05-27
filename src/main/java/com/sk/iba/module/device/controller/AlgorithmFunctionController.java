package com.sk.iba.module.device.controller;

import com.sk.iba.common.log.OperationLog;
import com.sk.iba.common.log.OperationType;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.common.result.Result;
import com.sk.iba.module.device.dto.FunctionCreateDTO;
import com.sk.iba.module.device.dto.FunctionQueryDTO;
import com.sk.iba.module.device.dto.FunctionUpdateDTO;
import com.sk.iba.module.device.service.AlgorithmFunctionService;
import com.sk.iba.module.device.vo.FunctionOptionVO;
import com.sk.iba.module.device.vo.FunctionVO;
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

import java.util.List;

/**
 * @author zzy
 */
@Tag(name = "算法功能管理模块", description = "算法功能的增删改查及分页查询")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/device/function")
public class AlgorithmFunctionController {

    private final AlgorithmFunctionService algorithmFunctionService;

    @OperationLog(module = "算法功能管理", name = "分页查询算法功能", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "分页查询算法功能", description = "根据功能名称、编码、状态进行分页查询")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('device:function:list')")
    public Result<PageResult<FunctionVO>> page(@ParameterObject @Validated FunctionQueryDTO queryDTO) {
        return Result.success(algorithmFunctionService.pageFunctions(queryDTO));
    }

    @OperationLog(module = "算法功能管理", name = "获取算法功能详情", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "获取算法功能详情")
    @Parameter(name = "id", description = "功能ID", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('device:function:detail')")
    public Result<FunctionVO> getById(@PathVariable @NotNull(message = "功能ID不能为空") Long id) {
        return Result.success(algorithmFunctionService.getFunctionById(id));
    }

    @OperationLog(module = "算法功能管理", name = "创建算法功能", type = OperationType.CREATE)
    @Operation(summary = "创建算法功能", description = "新增算法功能，成功后返回主键ID")
    @PostMapping
    @PreAuthorize("hasAuthority('device:function:create')")
    public Result<Long> create(@RequestBody @Valid FunctionCreateDTO createDTO) {
        return Result.success(algorithmFunctionService.createFunction(createDTO));
    }

    @OperationLog(module = "算法功能管理", name = "修改算法功能", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "修改算法功能", description = "根据 ID 修改算法功能")
    @PutMapping
    @PreAuthorize("hasAuthority('device:function:update')")
    public Result<Void> update(@RequestBody @Valid FunctionUpdateDTO updateDTO) {
        algorithmFunctionService.updateFunction(updateDTO);
        return Result.success();
    }

    @OperationLog(module = "算法功能管理", name = "删除算法功能", type = OperationType.DELETE, recordResult = false)
    @Operation(summary = "逻辑删除算法功能", description = "根据 ID 逻辑删除算法功能")
    @Parameter(name = "id", description = "功能ID", required = true, example = "1")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('device:function:delete')")
    public Result<Void> delete(@PathVariable @NotNull(message = "功能ID不能为空") Long id) {
        algorithmFunctionService.deleteFunction(id);
        return Result.success();
    }

    @OperationLog(module = "算法功能管理", name = "查询启用算法功能下拉选项", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "查询启用算法功能下拉选项", description = "用于摄像头绑定功能时选择功能")
    @GetMapping("/options")
    @PreAuthorize("hasAuthority('device:function:options')")
    public Result<List<FunctionOptionVO>> options() {
        return Result.success(algorithmFunctionService.listEnabledFunctionOptions());
    }
}