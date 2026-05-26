package com.sk.iba.module.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 摄像头分配功能参数
 *
 * @author zzy
 */
@Schema(description = "摄像头分配功能参数")
@Data
public class CameraAssignFunctionDTO {

    @Schema(description = "摄像头ID", example = "1")
    private Long cameraId;

    @Schema(description = "功能ID列表")
    private List<Long> functionIds;
}