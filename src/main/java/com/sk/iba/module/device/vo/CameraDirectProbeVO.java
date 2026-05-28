package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 直连摄像头识别结果
 *
 * @author zzy
 */
@Schema(description = "直连摄像头识别结果")
@Data
public class CameraDirectProbeVO {

    @Schema(description = "摄像头名称")
    private String cameraName;

    @Schema(description = "摄像头编码")
    private String cameraCode;

    @Schema(description = "视频源类型：1 RTSP")
    private Integer sourceType;

    @Schema(description = "默认视频源地址")
    private String sourceUrl;

    @Schema(description = "摄像头IP")
    private String ip;

    @Schema(description = "RTSP端口")
    private Integer port;

    @Schema(description = "账号")
    private String username;

    @Schema(description = "密码")
    private String password;
}