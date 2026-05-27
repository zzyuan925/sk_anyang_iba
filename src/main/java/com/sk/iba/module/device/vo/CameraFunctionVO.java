package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 摄像头已绑定功能返回对象
 *
 * @author zzy
 */
@Schema(description = "摄像头已绑定功能返回对象")
@Data
public class CameraFunctionVO {

    @Schema(description = "摄像头功能关联ID", example = "1")
    private Long cameraFunctionId;

    @Schema(description = "功能ID", example = "1")
    private Long functionId;

    @Schema(description = "功能名称", example = "安全帽")
    private String functionName;

    @Schema(description = "功能编码", example = "AQM1")
    private String functionCode;

    @Schema(description = "功能类型", example = "1121")
    private String functionType;

    @Schema(description = "是否配置ROI：0未配置，1已配置", example = "0")
    private Integer roiConfigured;

    @Schema(description = "ROI数量", example = "2")
    private Integer roiCount;
    
    @Schema(description = "ROI显示文本：全屏 / 已配置", example = "全屏")
    private String roiText;

    @Schema(description = "是否配置运行时间：0未配置，1已配置", example = "0")
    private Integer timeConfigured;

    @Schema(description = "运行时间显示文本：全天 / 已配置", example = "全天")
    private String timeText;
}