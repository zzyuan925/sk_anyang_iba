package com.sk.iba.module.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增摄像头功能ROI参数
 *
 * @author zzy
 */
@Schema(description = "新增摄像头功能ROI参数")
@Data
public class CameraFunctionRoiCreateDTO {

    @Schema(description = "ROI昵称", example = "门口区域")
    @Size(max = 100, message = "ROI昵称长度不能超过 100")
    private String roiName;

    @Schema(description = "ROI类型ID", example = "1")
    @NotNull(message = "ROI类型不能为空")
    private Long roiTypeId;

    @Schema(description = "ROI多边形坐标JSON")
    @NotBlank(message = "ROI坐标不能为空")
    @Size(max = 5000, message = "ROI坐标长度不能超过 5000")
    private String roiData;
}