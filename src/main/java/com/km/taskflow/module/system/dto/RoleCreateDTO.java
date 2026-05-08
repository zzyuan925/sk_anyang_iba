package com.km.taskflow.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增角色参数
 *
 * @author zzy
 */
@Schema(description = "新增角色参数")
@Data
public class RoleCreateDTO {

    @Schema(description = "角色名称", example = "项目经理")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过 50")
    private String roleName;

    @Schema(description = "角色编码", example = "project_manager")
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过 50")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "角色编码只能由字母、数字、下划线组成，并且必须以字母开头")
    private String roleCode;

    @Schema(description = "角色描述", example = "负责项目和任务分配")
    @Size(max = 255, message = "角色描述长度不能超过 255")
    private String description;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    @Min(value = 0, message = "状态值不正确")
    @Max(value = 1, message = "状态值不正确")
    private Integer status = 1;
}