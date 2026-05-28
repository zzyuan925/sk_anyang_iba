package com.sk.iba.module.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 批量误报标记参数
 *
 * @author zzy
 */
@Schema(description = "批量误报标记参数")
@Data
public class AlarmFalseAlarmBatchDTO {

    @Schema(description = "告警ID列表")
    @NotEmpty(message = "告警ID列表不能为空")
    private List<Long> ids;

    @Schema(description = "误报备注")
    @Size(max = 500, message = "误报备注不能超过500个字符")
    private String falseAlarmRemark;
}