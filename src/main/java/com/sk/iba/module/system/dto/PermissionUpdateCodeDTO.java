package com.sk.iba.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改权限编码参数
 *
 * @author zzy
 */
@Schema(description = "修改权限编码参数")
@Data
public class PermissionUpdateCodeDTO {

    @Schema(description = "权限编码", example = "system:user:list")
    @NotBlank(message = "权限编码不能为空")
    @Size(max = 100, message = "权限编码长度不能超过 100")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9:._-]*$", message = "权限编码格式不正确")
    private String permissionCode;
}