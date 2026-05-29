package com.sk.iba.module.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 部署算法包参数
 *
 * @author zzy
 */
@Schema(description = "部署算法包参数")
@Data
public class AlgorithmRuntimeDeployDTO {

    @Schema(description = "算法服务器ID")
    @NotNull(message = "算法服务器ID不能为空")
    private Long serverId;

    @Schema(description = "算法包ID")
    @NotNull(message = "算法包ID不能为空")
    private Long algorithmPackageId;
}