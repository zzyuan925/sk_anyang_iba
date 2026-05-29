package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 算法包下拉选项
 *
 * @author zzy
 */
@Schema(description = "算法包下拉选项")
@Data
public class AlgorithmPackageOptionVO {

    @Schema(description = "算法包ID")
    private Long id;

    @Schema(description = "算法功能ID")
    private Long functionId;

    @Schema(description = "算法功能名称")
    private String functionName;

    @Schema(description = "算法功能编码")
    private String functionCode;

    @Schema(description = "算法包版本")
    private String version;

    @Schema(description = "启动环境")
    private String runtimeEnv;

    @Schema(description = "下拉显示名称")
    private String label;
}