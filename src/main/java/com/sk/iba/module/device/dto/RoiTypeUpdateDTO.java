package com.sk.iba.module.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改ROI类型参数
 *
 * @author zzy
 */
@Schema(description = "修改ROI类型参数")
@Data
public class RoiTypeUpdateDTO {

    @Schema(description = "ROI类型ID", example = "1")
    @NotNull(message = "ROI类型ID不能为空")
    private Long id;

    @Schema(description = "ROI类型名称", example = "检测区域")
    @Size(max = 100, message = "ROI类型名称长度不能超过 100")
    private String typeName;

    @Schema(description = "ROI类型编码", example = "detect_area")
    @Size(max = 50, message = "ROI类型编码长度不能超过 50")
    @Pattern(regexp = "^[a-zA-Z0-9_:-]+$", message = "ROI类型编码只能由字母、数字、下划线、冒号、中横线组成")
    private String typeCode;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    @Size(max = 255, message = "备注长度不能超过 255")
    private String remark;
}