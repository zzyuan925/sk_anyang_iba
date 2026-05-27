package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 摄像头功能ROI返回对象
 *
 * @author zzy
 */
@Schema(description = "摄像头功能ROI返回对象")
@Data
public class CameraFunctionRoiVO {

    @Schema(description = "摄像头功能关联ID", example = "1")
    private Long cameraFunctionId;

    @Schema(description = "ROI多边形坐标JSON")
    private String roiData;

    @Schema(description = "是否配置ROI：0未配置，1已配置", example = "0")
    private Integer roiConfigured;

    @Schema(description = "ROI显示文本：全屏 / 已配置", example = "全屏")
    private String roiText;
}