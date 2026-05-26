package com.sk.iba.module.device.dto;

import com.sk.iba.common.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 摄像头分页查询参数
 *
 * @author zzy
 */
@Schema(description = "摄像头分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class CameraQueryDTO extends PageQuery {

    @Schema(description = "摄像头名称", example = "入口")
    private String cameraName;

    @Schema(description = "摄像头编码", example = "camera_001")
    private String cameraCode;

    @Schema(description = "所属区域ID", example = "1")
    private Long regionId;

    @Schema(description = "视频源类型：1 RTSP，2 本地视频文件", example = "1")
    private Integer sourceType;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;
}