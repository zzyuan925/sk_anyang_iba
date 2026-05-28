package com.sk.iba.module.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 直连摄像头识别参数
 *
 * @author zzy
 */
@Schema(description = "直连摄像头识别参数")
@Data
public class CameraDirectProbeDTO {

    @Schema(description = "摄像头IP", example = "192.168.1.64")
    @NotBlank(message = "摄像头IP不能为空")
    @Size(max = 64, message = "摄像头IP长度不能超过 64")
    private String ip;

    @Schema(description = "RTSP端口，用于生成RTSP地址；不传默认554", example = "554")
    @Min(value = 1, message = "端口不能小于 1")
    @Max(value = 65535, message = "端口不能大于 65535")
    private Integer port = 554;

    @Schema(description = "账号", example = "admin")
    @NotBlank(message = "账号不能为空")
    @Size(max = 100, message = "账号长度不能超过 100")
    private String username;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(max = 255, message = "密码长度不能超过 255")
    private String password;
}