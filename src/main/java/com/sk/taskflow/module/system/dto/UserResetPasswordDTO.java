package com.sk.taskflow.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置用户密码参数
 *
 * @author zzy
 */
@Schema(description = "重置用户密码参数")
@Data
public class UserResetPasswordDTO {

    @Schema(description = "新密码", example = "123456")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 100, message = "新密码长度必须在 6 到 100 之间")
    private String newPassword;
}