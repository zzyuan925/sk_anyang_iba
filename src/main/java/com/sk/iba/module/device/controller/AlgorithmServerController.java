package com.sk.iba.module.device.controller;

import com.sk.iba.common.log.OperationLog;
import com.sk.iba.common.log.OperationType;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.common.result.Result;
import com.sk.iba.module.device.dto.AlgorithmServerCreateDTO;
import com.sk.iba.module.device.dto.AlgorithmServerQueryDTO;
import com.sk.iba.module.device.dto.AlgorithmServerUpdateDTO;
import com.sk.iba.module.device.service.AlgorithmServerService;
import com.sk.iba.module.device.vo.AlgorithmServerOptionVO;
import com.sk.iba.module.device.vo.AlgorithmServerVO;
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
@Tag(name = "算法服务器管理模块", description = "算法服务器的增删改查及分页查询")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/device/algorithm-server")
public class AlgorithmServerController {

    private final AlgorithmServerService algorithmServerService;

    @OperationLog(module = "算法服务器管理", name = "分页查询算法服务器", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "分页查询算法服务器", description = "根据服务器名、IP、状态进行分页查询")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('device:algorithm-server:list')")
    public Result<PageResult<AlgorithmServerVO>> page(@ParameterObject @Validated AlgorithmServerQueryDTO queryDTO) {
        return Result.success(algorithmServerService.pageServers(queryDTO));
    }

    @OperationLog(module = "算法服务器管理", name = "获取算法服务器详情", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "获取算法服务器详情")
    @Parameter(name = "id", description = "服务器ID", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('device:algorithm-server:detail')")
    public Result<AlgorithmServerVO> getById(@PathVariable @NotNull(message = "服务器ID不能为空") Long id) {
        return Result.success(algorithmServerService.getServerById(id));
    }

    @OperationLog(module = "算法服务器管理", name = "创建算法服务器", type = OperationType.CREATE)
    @Operation(summary = "创建算法服务器", description = "新增算法服务器，成功后返回主键ID")
    @PostMapping
    @PreAuthorize("hasAuthority('device:algorithm-server:create')")
    public Result<Long> create(@RequestBody @Valid AlgorithmServerCreateDTO createDTO) {
        return Result.success(algorithmServerService.createServer(createDTO));
    }

    @OperationLog(module = "算法服务器管理", name = "修改算法服务器", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "修改算法服务器", description = "根据 ID 修改算法服务器")
    @PutMapping
    @PreAuthorize("hasAuthority('device:algorithm-server:update')")
    public Result<Void> update(@RequestBody @Valid AlgorithmServerUpdateDTO updateDTO) {
        algorithmServerService.updateServer(updateDTO);
        return Result.success();
    }

    @OperationLog(module = "算法服务器管理", name = "删除算法服务器", type = OperationType.DELETE, recordResult = false)
    @Operation(summary = "逻辑删除算法服务器", description = "根据 ID 逻辑删除算法服务器")
    @Parameter(name = "id", description = "服务器ID", required = true, example = "1")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('device:algorithm-server:delete')")
    public Result<Void> delete(@PathVariable @NotNull(message = "服务器ID不能为空") Long id) {
        algorithmServerService.deleteServer(id);
        return Result.success();
    }

    @OperationLog(module = "算法服务器管理", name = "查询启用算法服务器下拉选项", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "查询启用算法服务器下拉选项", description = "用于算法包部署时选择服务器")
    @GetMapping("/options")
    @PreAuthorize("hasAuthority('device:algorithm-server:options')")
    public Result<List<AlgorithmServerOptionVO>> options() {
        return Result.success(algorithmServerService.listEnabledServerOptions());
    }
}