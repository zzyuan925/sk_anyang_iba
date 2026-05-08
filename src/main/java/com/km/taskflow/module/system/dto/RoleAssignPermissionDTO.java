package com.km.taskflow.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色分配权限参数
 *
 * @author zzy
 */
@Schema(description = "角色分配权限参数")
@Data
public class RoleAssignPermissionDTO {

    @Schema(description = "角色ID", example = "1")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "权限ID列表", example = "[1,2,3]")
    private List<Long> permissionIds;
}