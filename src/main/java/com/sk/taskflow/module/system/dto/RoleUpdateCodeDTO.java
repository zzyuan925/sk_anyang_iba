package com.sk.taskflow.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改角色编码参数
 *
 * @author zzy
 */
@Schema(description = "修改角色编码参数")
@Data
public class RoleUpdateCodeDTO {

    @Schema(description = "角色编码", example = "project_manager")
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过 50")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "角色编码只能由字母、数字、下划线组成，并且必须以字母开头")
    private String roleCode;
}