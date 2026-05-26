package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 摄像头返回对象
 *
 * @author zzy
 */
@Schema(description = "摄像头返回对象")
@Data
public class CameraVO {

    @Schema(description = "摄像头ID", example = "1")
    private Long id;

    @Schema(description = "摄像头名称", example = "一号楼入口摄像头")
    private String cameraName;

    @Schema(description = "摄像头编码", example = "camera_001")
    private String cameraCode;

    @Schema(description = "所属区域ID", example = "1")
    private Long regionId;

    @Schema(description = "视频源类型：1 RTSP，2 本地视频文件", example = "1")
    private Integer sourceType;

    @Schema(description = "视频源地址")
    private String sourceUrl;

    @Schema(description = "摄像头IP")
    private String ip;

    @Schema(description = "端口")
    private Integer port;

    @Schema(description = "账号")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}