package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ROI类型下拉选项
 *
 * @author zzy
 */
@Schema(description = "ROI类型下拉选项")
@Data
public class RoiTypeOptionVO {

    @Schema(description = "ROI类型ID", example = "1")
    private Long id;

    @Schema(description = "ROI类型名称", example = "检测区域")
    private String typeName;

    @Schema(description = "ROI类型编码", example = "detect_area")
    private String typeCode;
}