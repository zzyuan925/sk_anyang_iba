package com.sk.iba.module.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增流媒体服务器参数
 *
 * @author zzy
 */
@Schema(description = "新增流媒体服务器参数")
@Data
public class MediaServerCreateDTO {

    @Schema(description = "流媒体服务器名称", example = "本地ZLM")
    @NotBlank(message = "流媒体服务器名称不能为空")
    @Size(max = 100, message = "流媒体服务器名称长度不能超过100")
    private String serverName;

    @Schema(description = "ZLM API地址，例如 http://127.0.0.1:8080")
    @NotBlank(message = "ZLM API地址不能为空")
    @Size(max = 255, message = "ZLM API地址长度不能超过255")
    private String apiBaseUrl;

    @Schema(description = "ZLM API密钥")
    @NotBlank(message = "ZLM API密钥不能为空")
    @Size(max = 100, message = "ZLM API密钥长度不能超过100")
    private String secret;

    @Schema(description = "HTTP播放基础地址，例如 http://127.0.0.1:8080")
    @NotBlank(message = "HTTP播放基础地址不能为空")
    @Size(max = 255, message = "HTTP播放基础地址长度不能超过255")
    private String httpPlayBaseUrl;

    @Schema(description = "WebSocket播放基础地址，例如 ws://127.0.0.1:8080")
    @Size(max = 255, message = "WebSocket播放基础地址长度不能超过255")
    private String wsPlayBaseUrl;

    @Schema(description = "WebRTC播放基础地址")
    @Size(max = 255, message = "WebRTC播放基础地址长度不能超过255")
    private String rtcPlayBaseUrl;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}