package com.sk.iba.module.device.dto;

import com.sk.iba.common.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增ROI类型参数
 *
 * @author zzy
 */
@Schema(description = "新增ROI类型参数")
@Data
public class RoiTypeCreateDTO {

    @Schema(description = "ROI类型名称", example = "检测区域")
    @NotBlank(message = "ROI类型名称不能为空")
    @Size(max = 100, message = "ROI类型名称长度不能超过 100")
    private String typeName;

    @Schema(description = "ROI类型编码", example = "detect_area")
    @NotBlank(message = "ROI类型编码不能为空")
    @Size(max = 50, message = "ROI类型编码长度不能超过 50")
    @Pattern(regexp = "^[a-zA-Z0-9_:-]+$", message = "ROI类型编码只能由字母、数字、下划线、冒号、中横线组成")
    private String typeCode;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status = StatusEnum.ENABLED.getCode();

    @Schema(description = "备注")
    @Size(max = 255, message = "备注长度不能超过 255")
    private String remark;
}