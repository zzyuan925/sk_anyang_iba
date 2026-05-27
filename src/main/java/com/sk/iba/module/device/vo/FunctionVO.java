package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 算法功能返回对象
 *
 * @author zzy
 */
@Schema(description = "算法功能返回对象")
@Data
public class FunctionVO {

    @Schema(description = "功能ID", example = "1")
    private Long id;

    @Schema(description = "功能名称", example = "抽烟检测")
    private String functionName;

    @Schema(description = "功能编码", example = "smoking_detect")
    private String functionCode;

    @Schema(description = "功能类型", example = "1121")
    private String functionType;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}