package com.km.taskflow.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改角色参数
 *
 * @author zzy
 */
@Schema(description = "修改角色参数")
@Data
public class RoleUpdateDTO {

    @Schema(description = "角色ID", example = "1")
    @NotNull(message = "角色ID不能为空")
    private Long id;

    @Schema(description = "角色名称", example = "项目经理")
    @Size(max = 50, message = "角色名称长度不能超过 50")
    private String roleName;

    @Schema(description = "角色描述", example = "负责项目和任务分配")
    @Size(max = 255, message = "角色描述长度不能超过 255")
    private String description;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;
}