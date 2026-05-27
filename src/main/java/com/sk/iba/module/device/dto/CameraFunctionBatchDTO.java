package com.sk.iba.module.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 摄像头功能批量操作参数
 *
 * @author zzy
 */
@Schema(description = "摄像头功能批量操作参数")
@Data
public class CameraFunctionBatchDTO {

    @Schema(description = "功能ID列表")
    private List<Long> functionIds;
}