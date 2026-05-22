package com.sk.iba.module.system.controller;

import com.sk.iba.common.log.OperationLog;
import com.sk.iba.common.log.OperationType;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.common.result.Result;
import com.sk.iba.module.system.dto.*;
import com.sk.iba.module.system.service.SysUserService;
import com.sk.iba.module.system.vo.RoleVO;
import com.sk.iba.module.system.vo.UserVO;
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
@Tag(name = "用户管理模块", description = "系统用户的增删改查及分页查询")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/user")
public class SysUserController {

    private final SysUserService sysUserService;

    @OperationLog(module = "用户管理", name = "分页查询用户", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "分页查询用户", description = "根据用户名、姓名、状态进行模糊分页查询")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<PageResult<UserVO>> page(@ParameterObject @Validated UserQueryDTO queryDTO) {
        return Result.success(sysUserService.pageUsers(queryDTO));
    }

    @OperationLog(module = "用户管理", name = "获取用户详情", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "获取用户详情")
    @Parameter(name = "id", description = "用户唯一标识", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:detail')")
    public Result<UserVO> getById(@PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        return Result.success(sysUserService.getUserById(id));
    }

    @OperationLog(module = "用户管理", name = "创建用户", type = OperationType.CREATE)
    @Operation(summary = "创建用户", description = "新增系统用户，成功后返回主键ID")
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:create')")
    public Result<Long> create(@RequestBody @Valid UserCreateDTO createDTO) {
        return Result.success(sysUserService.createUser(createDTO));
    }

    @OperationLog(module = "用户管理", name = "修改用户", type = OperationType.UPDATE,recordResult = false)
    @Operation(summary = "修改用户", description = "根据 ID 修改用户信息")
    @PutMapping
    @PreAuthorize("hasAuthority('system:user:update')")
    public Result<Void> update(@RequestBody @Valid UserUpdateDTO updateDTO) {
        sysUserService.updateUser(updateDTO);
        return Result.success();
    }

    @OperationLog(module = "用户管理", name = "删除用户", type = OperationType.DELETE,recordResult = false)
    @Operation(summary = "逻辑删除用户", description = "根据 ID 逻辑删除用户，数据不会从数据库彻底抹除")
    @Parameter(name = "id", description = "用户唯一标识", required = true, example = "1")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<Void> delete(@PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        sysUserService.deleteUser(id);
        return Result.success();
    }

    @OperationLog(module = "用户管理", name = "查询用户已绑定角色", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "查询用户已绑定角色", description = "根据用户ID查询该用户拥有的角色列表")
    @Parameter(name = "userId", description = "用户ID", required = true, example = "1")
    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('system:user:listRoles')")
    public Result<List<RoleVO>> listUserRoles(@PathVariable @NotNull(message = "用户ID不能为空") Long userId) {
        return Result.success(sysUserService.listUserRoles(userId));
    }

    @OperationLog(module = "用户管理", name = "给用户分配角色", type = OperationType.ASSIGN, recordResult = false)
    @Operation(summary = "给用户分配角色", description = "重新分配用户角色，会覆盖原有角色")
    @Parameter(name = "userId", description = "用户ID", required = true, example = "1")
    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('system:user:assignRole')")
    public Result<Void> assignRoles(@PathVariable @NotNull(message = "用户ID不能为空") Long userId,
                                    @RequestBody @Valid UserAssignRoleDTO assignRoleDTO) {
        assignRoleDTO.setUserId(userId);
        sysUserService.assignRoles(assignRoleDTO);
        return Result.success();
    }

    @OperationLog(module = "用户管理", name = "修改用户名", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "修改用户名", description = "根据用户ID修改登录用户名")
    @PreAuthorize("hasAuthority('system:user:updateUsername')")
    @PutMapping("/{id}/username")
    public Result<Void> updateUsername(@PathVariable @NotNull(message = "用户ID不能为空") Long id,
                                       @RequestBody @Valid UserUpdateUsernameDTO updateUsernameDTO) {
        sysUserService.updateUsername(id, updateUsernameDTO);
        return Result.success();
    }

    @OperationLog(module = "用户管理", name = "当前用户修改自己的密码", type = OperationType.CHANGE_PASSWORD, recordResult = false)
    @Operation(summary = "当前用户修改自己的密码")
    @PreAuthorize("hasAuthority('system:user:changePassword')")
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody @Valid UserChangePasswordDTO changePasswordDTO) {
        sysUserService.changePassword(changePasswordDTO);
        return Result.success();
    }

    @OperationLog(module = "用户管理", name = "管理员重置用户密码", type = OperationType.RESET_PASSWORD, recordResult = false)
    @Operation(summary = "管理员重置用户密码")
    @PreAuthorize("hasAuthority('system:user:resetPassword')")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable @NotNull(message = "用户ID不能为空") Long id,
                                      @RequestBody @Valid UserResetPasswordDTO resetPasswordDTO) {
        sysUserService.resetPassword(id, resetPasswordDTO);
        return Result.success();
    }
}