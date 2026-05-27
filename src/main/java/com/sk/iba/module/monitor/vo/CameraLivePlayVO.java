package com.sk.iba.module.monitor.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 摄像头直播播放信息
 *
 * @author zzy
 */
@Schema(description = "摄像头直播播放信息")
@Data
public class CameraLivePlayVO {

    @Schema(description = "摄像头ID", example = "1")
    private Long cameraId;

    @Schema(description = "摄像头名称", example = "一号楼入口摄像头")
    private String cameraName;

    @Schema(description = "摄像头编码", example = "camera_001")
    private String cameraCode;

    @Schema(description = "流媒体服务器ID", example = "1")
    private Long mediaServerId;

    @Schema(description = "ZLM应用名", example = "live")
    private String streamApp;

    @Schema(description = "ZLM流ID", example = "camera_1")
    private String streamId;

    @Schema(description = "播放协议：flv、ws-flv、webrtc、hls", example = "flv")
    private String playProtocol;

    @Schema(description = "播放地址")
    private String playUrl;

    @Schema(description = "流状态：0已关闭，1在线，2启动中", example = "1")
    private Integer streamStatus;
}