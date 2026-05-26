package com.sk.iba.module.device.dto;

import com.sk.iba.common.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 算法功能分页查询参数
 *
 * @author zzy
 */
@Schema(description = "算法功能分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class FunctionQueryDTO extends PageQuery {

    @Schema(description = "功能名称", example = "抽烟")
    private String functionName;

    @Schema(description = "功能编码", example = "smoking_detect")
    private String functionCode;

    @Schema(description = "功能类型", example = "1121")
    private String functionType;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;
}