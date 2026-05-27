package com.sk.iba.module.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

/**
 * 时间段参数
 *
 * @author zzy
 */
@Schema(description = "时间段参数")
@Data
public class TimePeriodDTO {

    @Schema(description = "开始时间", example = "08:00:00")
    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    @Schema(description = "结束时间", example = "18:00:00")
    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;
}