package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 摄像头功能配置返回对象
 *
 * @author zzy
 */
@Schema(description = "摄像头功能配置返回对象")
@Data
public class CameraFunctionConfigVO {

    @Schema(description = "摄像头ID", example = "1")
    private Long cameraId;

    @Schema(description = "摄像头名称", example = "一号楼入口摄像头")
    private String cameraName;

    @Schema(description = "已选择功能列表")
    private List<CameraFunctionVO> selectedFunctions;

    @Schema(description = "未选择功能列表")
    private List<FunctionOptionVO> unselectedFunctions;
}