package com.sk.iba.module.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 摄像头直播流实体
 *
 * @author zzy
 */
@Data
@TableName("biz_camera_live_stream")
public class CameraLiveStream {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 摄像头ID
     */
    private Long cameraId;

    /**
     * 流媒体服务器ID
     */
    private Long mediaServerId;

    /**
     * ZLM应用名
     */
    private String streamApp;

    /**
     * ZLM流ID
     */
    private String streamId;

    /**
     * 播放协议：flv、ws-flv、webrtc、hls
     */
    private String playProtocol;

    /**
     * 流状态：0已关闭，1在线，2启动中
     */
    private Integer streamStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}