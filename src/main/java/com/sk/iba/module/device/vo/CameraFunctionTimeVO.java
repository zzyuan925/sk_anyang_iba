package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalTime;

/**
 * 摄像头功能运行时间段返回对象
 *
 * @author zzy
 */
@Schema(description = "摄像头功能运行时间段返回对象")
@Data
public class CameraFunctionTimeVO {

    @Schema(description = "时间段ID", example = "1")
    private Long id;

    @Schema(description = "摄像头功能关联ID", example = "1")
    private Long cameraFunctionId;

    @Schema(description = "开始时间", example = "08:00:00")
    private LocalTime startTime;

    @Schema(description = "结束时间", example = "18:00:00")
    private LocalTime endTime;
}