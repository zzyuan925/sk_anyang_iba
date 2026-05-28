package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 平台摄像头信息
 *
 * @author zzy
 */
@Schema(description = "平台摄像头信息")
@Data
public class PlatformCameraVO {

    @Schema(description = "平台监控点唯一标识")
    private String indexCode;

    @Schema(description = "摄像头名称")
    private String name;
}