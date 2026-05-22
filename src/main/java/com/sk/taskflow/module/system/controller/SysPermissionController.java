package com.sk.taskflow.module.system.controller;

import com.sk.taskflow.common.log.OperationLog;
import com.sk.taskflow.common.log.OperationType;
import com.sk.taskflow.common.page.PageResult;
import com.sk.taskflow.common.result.Result;
import com.sk.taskflow.module.system.dto.PermissionCreateDTO;
import com.sk.taskflow.module.system.dto.PermissionQueryDTO;
import com.sk.taskflow.module.system.dto.PermissionUpdateCodeDTO;
import com.sk.taskflow.module.system.dto.PermissionUpdateDTO;
import com.sk.taskflow.module.system.service.SysPermissionService;
import com.sk.taskflow.module.system.vo.PermissionOptionVO;
import com.sk.taskflow.module.system.vo.PermissionVO;
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
@Tag(name = "权限管理模块", description = "系统权限的增删改查及下拉查询")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/permission")
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;

    @OperationLog(module = "权限管理", name = "分页查询权限", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "分页查询权限", description = "根据权限名称、权限编码、权限类型、状态进行分页查询")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:permission:list')")
    public Result<PageResult<PermissionVO>> page(@ParameterObject @Validated PermissionQueryDTO queryDTO) {
        return Result.success(sysPermissionService.pagePermissions(queryDTO));
    }

    @OperationLog(module = "权限管理", name = "获取权限详情", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "获取权限详情")
    @Parameter(name = "id", description = "权限ID", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:detail')")
    public Result<PermissionVO> getById(@PathVariable @NotNull(message = "权限ID不能为空") Long id) {
        return Result.success(sysPermissionService.getPermissionById(id));
    }

    @OperationLog(module = "权限管理", name = "创建权限", type = OperationType.CREATE)
    @Operation(summary = "创建权限", description = "新增系统权限，成功后返回主键ID")
    @PostMapping
    @PreAuthorize("hasAuthority('system:permission:create')")
    public Result<Long> create(@RequestBody @Valid PermissionCreateDTO createDTO) {
        return Result.success(sysPermissionService.createPermission(createDTO));
    }

    @OperationLog(module = "权限管理", name = "修改权限", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "修改权限", description = "根据 ID 修改权限信息")
    @PutMapping
    @PreAuthorize("hasAuthority('system:permission:update')")
    public Result<Void> update(@RequestBody @Valid PermissionUpdateDTO updateDTO) {
        sysPermissionService.updatePermission(updateDTO);
        return Result.success();
    }

    @OperationLog(module = "权限管理", name = "删除权限", type = OperationType.DELETE, recordResult = false)
    @Operation(summary = "逻辑删除权限", description = "根据 ID 逻辑删除权限")
    @Parameter(name = "id", description = "权限ID", required = true, example = "1")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:delete')")
    public Result<Void> delete(@PathVariable @NotNull(message = "权限ID不能为空") Long id) {
        sysPermissionService.deletePermission(id);
        return Result.success();
    }

    @OperationLog(module = "权限管理", name = "查询启用权限下拉选项", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "查询启用权限下拉选项", description = "用于角色分配权限时选择权限")
    @GetMapping("/options")
    @PreAuthorize("hasAuthority('system:permission:options')")
    public Result<List<PermissionOptionVO>> options() {
        return Result.success(sysPermissionService.listEnabledPermissionOptions());
    }

    @OperationLog(module = "权限管理", name = "修改权限编码", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "修改权限编码", description = "根据权限ID修改权限编码")
    @PreAuthorize("hasAuthority('system:permission:updateCode')")
    @PutMapping("/{id}/code")
    public Result<Void> updatePermissionCode(@PathVariable @NotNull(message = "权限ID不能为空") Long id,
                                             @RequestBody @Valid PermissionUpdateCodeDTO updateCodeDTO) {
        sysPermissionService.updatePermissionCode(id, updateCodeDTO);
        return Result.success();
    }
}