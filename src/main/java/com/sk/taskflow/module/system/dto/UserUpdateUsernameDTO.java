package com.sk.taskflow.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改用户名参数
 *
 * @author zzy
 */
@Schema(description = "修改用户名参数")
@Data
public class UserUpdateUsernameDTO {

    @Schema(description = "新用户名", example = "new_admin")
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过 50")
    private String username;
}