package com.sk.iba.module.monitor.dto;

import lombok.Data;

/**
 * ZLM 流状态变化回调参数
 *
 * @author zzy
 */
@Data
public class ZlmStreamChangedDTO {

    /**
     * 应用名
     */
    private String app;

    /**
     * 流ID
     */
    private String stream;

    /**
     * 是否注册：true上线，false关闭
     */
    private Boolean regist;

    /**
     * 协议，例如 rtsp、rtmp、fmp4 等
     */
    private String schema;

    /**
     * ZLM 媒体服务器ID，暂时不强依赖
     */
    private String mediaServerId;
}