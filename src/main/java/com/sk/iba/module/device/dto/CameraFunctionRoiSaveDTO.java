package com.sk.iba.module.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 保存摄像头功能ROI参数
 *
 * @author zzy
 */
@Schema(description = "保存摄像头功能ROI参数")
@Data
public class CameraFunctionRoiSaveDTO {

    @Schema(description = "ROI多边形坐标JSON")
    @NotBlank(message = "ROI坐标不能为空")
    @Size(max = 5000, message = "ROI坐标长度不能超过 5000")
    private String roiData;
}