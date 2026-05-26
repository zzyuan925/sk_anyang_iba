package com.sk.iba.module.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sk.iba.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 摄像头实体
 *
 * @author zzy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("biz_camera")
public class Camera extends BaseEntity {

    private String cameraName;

    private String cameraCode;

    private Long regionId;

    /**
     * 视频源类型：1 RTSP，2 本地视频文件
     */
    private Integer sourceType;

    /**
     * 视频源地址：RTSP地址或本地视频文件路径
     */
    private String sourceUrl;

    private String ip;

    private Integer port;

    private String username;

    private String password;

    /**
     * 状态：0禁用，1启用
     */
    private Integer status;

    private String remark;
}