package com.sk.iba.module.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 误报标记参数
 *
 * @author zzy
 */
@Schema(description = "误报标记参数")
@Data
public class AlarmFalseAlarmDTO {

    @Schema(description = "误报备注")
    @Size(max = 500, message = "误报备注不能超过500个字符")
    private String falseAlarmRemark;
}