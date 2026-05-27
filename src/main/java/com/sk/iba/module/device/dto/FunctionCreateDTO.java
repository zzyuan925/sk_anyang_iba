package com.sk.iba.module.device.dto;

import com.sk.iba.common.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增算法功能参数
 *
 * @author zzy
 */
@Schema(description = "新增算法功能参数")
@Data
public class FunctionCreateDTO {

    @Schema(description = "功能名称", example = "抽烟检测")
    @NotBlank(message = "功能名称不能为空")
    @Size(max = 100, message = "功能名称长度不能超过 100")
    private String functionName;

    @Schema(description = "功能编码", example = "smoking_detect")
    @NotBlank(message = "功能编码不能为空")
    @Size(max = 50, message = "功能编码长度不能超过 50")
    @Pattern(regexp = "^[a-zA-Z0-9_:-]+$", message = "功能编码只能由字母、数字、下划线、冒号、中横线组成")
    private String functionCode;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status = StatusEnum.ENABLED.getCode();

    @Schema(description = "备注")
    @Size(max = 255, message = "备注长度不能超过 255")
    private String remark;
}