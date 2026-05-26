package com.sk.iba.module.system.dto;

import com.sk.iba.common.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增区域参数
 *
 * @author zzy
 */
@Schema(description = "新增区域参数")
@Data
public class RegionCreateDTO {

    @Schema(description = "父级区域ID，0表示根节点", example = "0")
    private Long parentId = 0L;

    @Schema(description = "区域名称", example = "一号楼")
    @NotBlank(message = "区域名称不能为空")
    @Size(max = 100, message = "区域名称长度不能超过 100")
    private String regionName;

    @Schema(description = "区域编码", example = "building_1")
    @NotBlank(message = "区域编码不能为空")
    @Size(max = 50, message = "区域编码长度不能超过 50")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "区域编码只能由字母、数字、下划线组成，并且必须以字母开头")
    private String regionCode;

    @Schema(description = "排序", example = "0")
    private Integer sort = 0;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status = StatusEnum.ENABLED.getCode();

    @Schema(description = "备注")
    @Size(max = 255, message = "备注长度不能超过 255")
    private String remark;
}