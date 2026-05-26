package com.sk.iba.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 区域下拉选项
 *
 * @author zzy
 */
@Schema(description = "区域下拉选项")
@Data
public class RegionOptionVO {

    @Schema(description = "区域ID", example = "1")
    private Long id;

    @Schema(description = "父级区域ID", example = "0")
    private Long parentId;

    @Schema(description = "区域名称", example = "一号楼")
    private String regionName;

    @Schema(description = "区域编码", example = "building_1")
    private String regionCode;

    @Schema(description = "排序", example = "0")
    private Integer sort;
}