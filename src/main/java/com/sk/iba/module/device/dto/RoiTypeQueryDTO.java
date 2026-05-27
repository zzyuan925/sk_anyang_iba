package com.sk.iba.module.device.dto;

import com.sk.iba.common.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ROI类型分页查询参数
 *
 * @author zzy
 */
@Schema(description = "ROI类型分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class RoiTypeQueryDTO extends PageQuery {

    @Schema(description = "ROI类型名称", example = "检测")
    private String typeName;

    @Schema(description = "ROI类型编码", example = "detect_area")
    private String typeCode;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;
}