package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 算法功能下拉选项
 *
 * @author zzy
 */
@Schema(description = "算法功能下拉选项")
@Data
public class FunctionOptionVO {

    @Schema(description = "功能ID", example = "1")
    private Long id;

    @Schema(description = "功能名称", example = "抽烟检测")
    private String functionName;

    @Schema(description = "功能编码", example = "smoking_detect")
    private String functionCode;
}