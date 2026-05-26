package com.sk.iba.module.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 修改摄像头参数
 *
 * @author zzy
 */
@Schema(description = "修改摄像头参数")
@Data
public class CameraUpdateDTO {

    @Schema(description = "摄像头ID", example = "1")
    @NotNull(message = "摄像头ID不能为空")
    private Long id;

    @Schema(description = "摄像头名称", example = "一号楼入口摄像头")
    @Size(max = 100, message = "摄像头名称长度不能超过 100")
    private String cameraName;

    @Schema(description = "摄像头编码", example = "camera_001")
    @Size(max = 50, message = "摄像头编码长度不能超过 50")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "摄像头编码只能由字母、数字、下划线组成，并且必须以字母开头")
    private String cameraCode;

    @Schema(description = "所属区域ID", example = "1")
    private Long regionId;

    @Schema(description = "视频源类型：1 RTSP，2 本地视频文件", example = "1")
    private Integer sourceType;

    @Schema(description = "视频源地址：RTSP地址或本地视频文件路径")
    @Size(max = 500, message = "视频源地址长度不能超过 500")
    private String sourceUrl;

    @Schema(description = "摄像头IP", example = "192.168.1.100")
    @Size(max = 64, message = "摄像头IP长度不能超过 64")
    private String ip;

    @Schema(description = "端口", example = "554")
    @Min(value = 1, message = "端口不能小于 1")
    @Max(value = 65535, message = "端口不能大于 65535")
    private Integer port;

    @Schema(description = "账号", example = "admin")
    @Size(max = 100, message = "账号长度不能超过 100")
    private String username;

    @Schema(description = "密码", example = "123456")
    @Size(max = 255, message = "密码长度不能超过 255")
    private String password;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    @Size(max = 255, message = "备注长度不能超过 255")
    private String remark;
}