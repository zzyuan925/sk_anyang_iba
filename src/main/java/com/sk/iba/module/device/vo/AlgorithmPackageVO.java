package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 算法包信息
 *
 * @author zzy
 */
@Schema(description = "算法包信息")
@Data
public class AlgorithmPackageVO {

    @Schema(description = "算法包ID")
    private Long id;

    @Schema(description = "算法功能ID")
    private Long functionId;

    @Schema(description = "算法功能名称")
    private String functionName;

    @Schema(description = "算法功能编码")
    private String functionCode;

    @Schema(description = "算法包路径")
    private String packagePath;

    @Schema(description = "算法包描述")
    private String description;

    @Schema(description = "算法包版本")
    private String version;

    @Schema(description = "启动环境")
    private String runtimeEnv;

    @Schema(description = "启动文件名")
    private String startFileName;

    @Schema(description = "权重文件路径")
    private String weightPath;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}