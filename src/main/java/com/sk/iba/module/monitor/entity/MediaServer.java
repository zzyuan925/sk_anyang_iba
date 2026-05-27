package com.sk.iba.module.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sk.iba.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流媒体服务器实体
 *
 * @author zzy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("biz_media_server")
public class MediaServer extends BaseEntity {

    /**
     * 流媒体服务器名称
     */
    private String serverName;

    /**
     * ZLM API 地址，例如 http://127.0.0.1:8080
     */
    private String apiBaseUrl;

    /**
     * ZLM API 密钥
     */
    private String secret;

    /**
     * HTTP 播放基础地址，例如 http://127.0.0.1:8080
     */
    private String httpPlayBaseUrl;

    /**
     * WebSocket 播放基础地址，例如 ws://127.0.0.1:8080
     */
    private String wsPlayBaseUrl;

    /**
     * WebRTC 播放基础地址
     */
    private String rtcPlayBaseUrl;

    /**
     * 状态：0禁用，1启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}