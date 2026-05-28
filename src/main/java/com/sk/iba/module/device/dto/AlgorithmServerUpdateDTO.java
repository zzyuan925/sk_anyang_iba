package com.sk.iba.module.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 修改算法服务器参数
 *
 * @author zzy
 */
@Schema(description = "修改算法服务器参数")
@Data
public class AlgorithmServerUpdateDTO {

    @Schema(description = "服务器ID", example = "1")
    @NotNull(message = "服务器ID不能为空")
    private Long id;

    @Schema(description = "服务器名", example = "算法服务器01")
    @Size(max = 100, message = "服务器名长度不能超过 100")
    private String serverName;

    @Schema(description = "账号", example = "root")
    @Size(max = 100, message = "账号长度不能超过 100")
    private String account;

    /**
     * 修改时为空表示不修改旧密码
     */
    @Schema(description = "密码，不传或传空表示不修改")
    @Size(max = 255, message = "密码长度不能超过 255")
    private String password;

    @Schema(description = "服务器IP", example = "192.168.1.100")
    @Size(max = 64, message = "服务器IP长度不能超过 64")
    private String ip;

    @Schema(description = "端口", example = "22")
    @Min(value = 1, message = "端口不能小于 1")
    @Max(value = 65535, message = "端口不能大于 65535")
    private Integer port;

    @Schema(description = "部署地址/部署目录", example = "/opt/algorithm")
    @Size(max = 255, message = "部署地址长度不能超过 255")
    private String deployPath;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;
}