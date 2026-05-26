package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 摄像头下拉选项
 *
 * @author zzy
 */
@Schema(description = "摄像头下拉选项")
@Data
public class CameraOptionVO {

    @Schema(description = "摄像头ID", example = "1")
    private Long id;

    @Schema(description = "摄像头名称", example = "一号楼入口摄像头")
    private String cameraName;

    @Schema(description = "摄像头编码", example = "camera_001")
    private String cameraCode;

    @Schema(description = "所属区域ID", example = "1")
    private Long regionId;
}