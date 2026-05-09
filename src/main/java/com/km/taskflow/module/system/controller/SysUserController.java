package com.km.taskflow.module.system.controller;

import com.km.taskflow.common.page.PageResult;
import com.km.taskflow.common.result.Result;
import com.km.taskflow.module.system.dto.UserAssignRoleDTO;
import com.km.taskflow.module.system.dto.UserCreateDTO;
import com.km.taskflow.module.system.dto.UserQueryDTO;
import com.km.taskflow.module.system.dto.UserUpdateDTO;
import com.km.taskflow.module.system.service.SysUserService;
import com.km.taskflow.module.system.vo.RoleVO;
import com.km.taskflow.module.system.vo.UserVO;
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

    @Operation(summary = "分页查询用户", description = "根据用户名、姓名、状态进行模糊分页查询")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<PageResult<UserVO>> page(@ParameterObject @Validated UserQueryDTO queryDTO) {
        return Result.success(sysUserService.pageUsers(queryDTO));
    }

    @Operation(summary = "获取用户详情")
    @Parameter(name = "id", description = "用户唯一标识", required = true, example = "1")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        return Result.success(sysUserService.getUserById(id));
    }

    @Operation(summary = "创建用户", description = "新增系统用户，成功后返回主键ID")
    @PostMapping
    public Result<Long> create(@RequestBody @Valid UserCreateDTO createDTO) {
        return Result.success(sysUserService.createUser(createDTO));
    }

    @Operation(summary = "修改用户", description = "根据 ID 修改用户信息")
    @PutMapping
    public Result<Void> update(@RequestBody @Valid UserUpdateDTO updateDTO) {
        sysUserService.updateUser(updateDTO);
        return Result.success();
    }

    @Operation(summary = "逻辑删除用户", description = "根据 ID 逻辑删除用户，数据不会从数据库彻底抹除")
    @Parameter(name = "id", description = "用户唯一标识", required = true, example = "1")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable @NotNull(message = "用户ID不能为空") Long id) {
        sysUserService.deleteUser(id);
        return Result.success();
    }
    
    @Operation(summary = "查询用户已绑定角色", description = "根据用户ID查询该用户拥有的角色列表")
    @Parameter(name = "userId", description = "用户ID", required = true, example = "1")
    @GetMapping("/{userId}/roles")
    public Result<List<RoleVO>> listUserRoles(@PathVariable @NotNull(message = "用户ID不能为空") Long userId) {
        return Result.success(sysUserService.listUserRoles(userId));
    }

    @Operation(summary = "给用户分配角色", description = "重新分配用户角色，会覆盖原有角色")
    @Parameter(name = "userId", description = "用户ID", required = true, example = "1")
    @PutMapping("/{userId}/roles")
    public Result<Void> assignRoles(@PathVariable @NotNull(message = "用户ID不能为空") Long userId,
                                    @RequestBody @Valid UserAssignRoleDTO assignRoleDTO) {
        assignRoleDTO.setUserId(userId);
        sysUserService.assignRoles(assignRoleDTO);
        return Result.success();
    }
}