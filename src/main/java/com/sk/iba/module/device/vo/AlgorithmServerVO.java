package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 算法服务器返回对象
 *
 * @author zzy
 */
@Schema(description = "算法服务器返回对象")
@Data
public class AlgorithmServerVO {

    @Schema(description = "服务器ID", example = "1")
    private Long id;

    @Schema(description = "服务器名", example = "算法服务器01")
    private String serverName;

    @Schema(description = "账号", example = "root")
    private String account;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "服务器IP", example = "192.168.1.100")
    private String ip;

    @Schema(description = "端口", example = "22")
    private Integer port;

    @Schema(description = "部署地址/部署目录", example = "/opt/algorithm")
    private String deployPath;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}