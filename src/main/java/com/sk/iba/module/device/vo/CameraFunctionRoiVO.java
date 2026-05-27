package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 摄像头功能ROI返回对象
 *
 * @author zzy
 */
@Schema(description = "摄像头功能ROI返回对象")
@Data
public class CameraFunctionRoiVO {

    @Schema(description = "ROI ID", example = "1")
    private Long id;

    @Schema(description = "摄像头功能关联ID", example = "1")
    private Long cameraFunctionId;

    @Schema(description = "ROI昵称", example = "门口区域")
    private String roiName;

    @Schema(description = "ROI类型ID", example = "1")
    private Long roiTypeId;

    @Schema(description = "ROI类型名称", example = "检测区域")
    private String roiTypeName;

    @Schema(description = "ROI类型编码", example = "detect_area")
    private String roiTypeCode;

    @Schema(description = "ROI多边形坐标JSON")
    private String roiData;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}