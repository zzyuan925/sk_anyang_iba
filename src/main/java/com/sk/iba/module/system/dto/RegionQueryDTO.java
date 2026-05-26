package com.sk.iba.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 区域查询参数
 *
 * @author zzy
 */
@Schema(description = "区域查询参数")
@Data
public class RegionQueryDTO {

    @Schema(description = "区域名称", example = "一号楼")
    private String regionName;

    @Schema(description = "区域编码", example = "building_1")
    private String regionCode;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;
}