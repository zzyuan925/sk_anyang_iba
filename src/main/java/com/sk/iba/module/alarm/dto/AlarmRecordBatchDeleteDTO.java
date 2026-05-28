package com.sk.iba.module.alarm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 告警记录批量删除参数
 *
 * @author zzy
 */
@Schema(description = "告警记录批量删除参数")
@Data
public class AlarmRecordBatchDeleteDTO {

    @Schema(description = "告警ID列表")
    @NotEmpty(message = "告警ID列表不能为空")
    private List<Long> ids;
}