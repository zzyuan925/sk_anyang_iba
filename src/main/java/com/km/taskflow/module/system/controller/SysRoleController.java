package com.km.taskflow.module.system.controller;

import com.km.taskflow.common.page.PageResult;
import com.km.taskflow.common.result.Result;
import com.km.taskflow.module.system.dto.RoleAssignPermissionDTO;
import com.km.taskflow.module.system.dto.RoleCreateDTO;
import com.km.taskflow.module.system.dto.RoleQueryDTO;
import com.km.taskflow.module.system.dto.RoleUpdateDTO;
import com.km.taskflow.module.system.service.SysRoleService;
import com.km.taskflow.module.system.vo.PermissionVO;
import com.km.taskflow.module.system.vo.RoleOptionVO;
import com.km.taskflow.module.system.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zzy
 */
@Tag(name = "角色管理模块", description = "系统角色的增删改查及分页查询")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/role")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Operation(summary = "分页查询角色", description = "根据角色名称、角色编码、状态进行分页查询")
    @GetMapping("/page")
    public Result<PageResult<RoleVO>> page(@ParameterObject @Validated RoleQueryDTO queryDTO) {
        return Result.success(sysRoleService.pageRoles(queryDTO));
    }

    @Operation(summary = "获取角色详情")
    @Parameter(name = "id", description = "角色ID", required = true, example = "1")
    @GetMapping("/{id}")
    public Result<RoleVO> getById(@PathVariable @NotNull(message = "角色ID不能为空") Long id) {
        return Result.success(sysRoleService.getRoleById(id));
    }

    @Operation(summary = "创建角色", description = "新增系统角色，成功后返回主键ID")
    @PostMapping
    public Result<Long> create(@RequestBody @Valid RoleCreateDTO createDTO) {
        return Result.success(sysRoleService.createRole(createDTO));
    }

    @Operation(summary = "修改角色", description = "根据 ID 修改角色信息")
    @PutMapping
    public Result<Void> update(@RequestBody @Valid RoleUpdateDTO updateDTO) {
        sysRoleService.updateRole(updateDTO);
        return Result.success();
    }

    @Operation(summary = "逻辑删除角色", description = "根据 ID 逻辑删除角色，数据不会从数据库彻底抹除")
    @Parameter(name = "id", description = "角色ID", required = true, example = "1")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @NotNull(message = "角色ID不能为空") Long id) {
        sysRoleService.deleteRole(id);
        return Result.success();
    }

    @Operation(summary = "查询启用角色下拉选项", description = "用于用户分配角色时选择角色")
    @GetMapping("/options")
    public Result<List<RoleOptionVO>> options() {
        return Result.success(sysRoleService.listEnabledRoleOptions());
    }

    @Operation(summary = "查询角色已绑定权限", description = "根据角色ID查询该角色拥有的权限列表")
    @Parameter(name = "roleId", description = "角色ID", required = true, example = "1")
    @GetMapping("/{roleId}/permissions")
    public Result<List<PermissionVO>> listRolePermissions(@PathVariable @NotNull(message = "角色ID不能为空") Long roleId) {
        return Result.success(sysRoleService.listRolePermissions(roleId));
    }

    @Operation(summary = "给角色分配权限", description = "重新分配角色权限，会覆盖原有权限")
    @Parameter(name = "roleId", description = "角色ID", required = true, example = "1")
    @PutMapping("/{roleId}/permissions")
    public Result<Void> assignPermissions(@PathVariable @NotNull(message = "角色ID不能为空") Long roleId,
                                          @RequestBody @Valid RoleAssignPermissionDTO assignPermissionDTO) {
        assignPermissionDTO.setRoleId(roleId);
        sysRoleService.assignPermissions(assignPermissionDTO);
        return Result.success();
    }
}