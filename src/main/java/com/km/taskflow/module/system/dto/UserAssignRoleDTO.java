package com.km.taskflow.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 用户分配角色参数
 *
 * @author zzy
 */
@Schema(description = "用户分配角色参数")
@Data
public class UserAssignRoleDTO {

    @Schema(description = "用户ID", example = "1")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "角色ID列表", example = "[1,2]")
    private List<Long> roleIds;
}