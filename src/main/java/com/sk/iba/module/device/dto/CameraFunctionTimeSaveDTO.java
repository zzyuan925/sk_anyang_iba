package com.sk.iba.module.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

/**
 * 保存摄像头功能运行时间段参数
 *
 * @author zzy
 */
@Schema(description = "保存摄像头功能运行时间段参数")
@Data
public class CameraFunctionTimeSaveDTO {

    @Schema(description = "运行时间段列表，空列表表示全天运行")
    @Valid
    private List<TimePeriodDTO> times;
}