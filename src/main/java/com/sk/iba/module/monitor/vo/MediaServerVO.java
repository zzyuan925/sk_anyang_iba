package com.sk.iba.module.monitor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流媒体服务器返回对象
 *
 * @author zzy
 */
@Schema(description = "流媒体服务器返回对象")
@Data
public class MediaServerVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "流媒体服务器名称", example = "本地ZLM")
    private String serverName;

    @Schema(description = "ZLM API地址，例如 http://127.0.0.1:8080")
    private String apiBaseUrl;

    @Schema(description = "ZLM API密钥")
    private String secret;

    @Schema(description = "HTTP播放基础地址，例如 http://127.0.0.1:8080")
    private String httpPlayBaseUrl;

    @Schema(description = "WebSocket播放基础地址，例如 ws://127.0.0.1:8080")
    private String wsPlayBaseUrl;

    @Schema(description = "WebRTC播放基础地址")
    private String rtcPlayBaseUrl;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}