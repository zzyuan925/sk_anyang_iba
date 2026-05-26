package com.sk.iba.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 区域树返回对象
 *
 * @author zzy
 */
@Schema(description = "区域树返回对象")
@Data
public class RegionTreeVO {

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

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "子区域")
    private List<RegionTreeVO> children = new ArrayList<>();
}