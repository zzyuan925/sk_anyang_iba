package com.sk.iba.module.device.dto;

import com.sk.iba.common.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 新增算法服务器参数
 *
 * @author zzy
 */
@Schema(description = "新增算法服务器参数")
@Data
public class AlgorithmServerCreateDTO {

    @Schema(description = "服务器名", example = "算法服务器01")
    @NotBlank(message = "服务器名不能为空")
    @Size(max = 100, message = "服务器名长度不能超过 100")
    private String serverName;

    @Schema(description = "账号", example = "root")
    @NotBlank(message = "账号不能为空")
    @Size(max = 100, message = "账号长度不能超过 100")
    private String account;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(max = 255, message = "密码长度不能超过 255")
    private String password;

    @Schema(description = "服务器IP", example = "192.168.1.100")
    @NotBlank(message = "服务器IP不能为空")
    @Size(max = 64, message = "服务器IP长度不能超过 64")
    private String ip;

    @Schema(description = "端口", example = "22")
    @NotNull(message = "端口不能为空")
    @Min(value = 1, message = "端口不能小于 1")
    @Max(value = 65535, message = "端口不能大于 65535")
    private Integer port;

    @Schema(description = "部署地址/部署目录", example = "/opt/algorithm")
    @NotBlank(message = "部署地址不能为空")
    @Size(max = 255, message = "部署地址长度不能超过 255")
    private String deployPath;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status = StatusEnum.ENABLED.getCode();

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;
}