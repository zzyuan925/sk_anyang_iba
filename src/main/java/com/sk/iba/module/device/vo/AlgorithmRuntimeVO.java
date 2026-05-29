package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 算法运行信息
 *
 * @author zzy
 */
@Schema(description = "算法运行信息")
@Data
public class AlgorithmRuntimeVO {

    @Schema(description = "算法运行ID")
    private Long id;

    @Schema(description = "算法服务器ID")
    private Long serverId;

    @Schema(description = "算法服务器名称")
    private String serverName;

    @Schema(description = "算法服务器IP")
    private String serverIp;

    @Schema(description = "算法功能ID")
    private Long functionId;

    @Schema(description = "算法功能名称")
    private String functionName;

    @Schema(description = "算法功能编码")
    private String functionCode;

    @Schema(description = "算法包ID")
    private Long algorithmPackageId;

    @Schema(description = "算法包版本")
    private String packageVersion;

    @Schema(description = "算法包原始路径")
    private String packagePath;

    @Schema(description = "算法包部署路径")
    private String deployPath;

    @Schema(description = "启动环境")
    private String runtimeEnv;

    @Schema(description = "启动文件名")
    private String startFileName;

    @Schema(description = "权重文件路径")
    private String weightPath;

    @Schema(description = "运行状态：0未运行，1运行中，2运行异常")
    private Integer runStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}