package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 算法服务器下拉选项
 *
 * @author zzy
 */
@Schema(description = "算法服务器下拉选项")
@Data
public class AlgorithmServerOptionVO {

    @Schema(description = "服务器ID", example = "1")
    private Long id;

    @Schema(description = "服务器名", example = "算法服务器01")
    private String serverName;

    @Schema(description = "服务器IP", example = "192.168.1.100")
    private String ip;

    @Schema(description = "端口", example = "22")
    private Integer port;

    @Schema(description = "部署地址/部署目录", example = "/opt/algorithm")
    private String deployPath;
}