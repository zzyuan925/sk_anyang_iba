package com.sk.iba.common.constant;

/**
 * 直播流相关常量
 *
 * @author zzy
 */
public class LiveStreamConstants {

    private LiveStreamConstants() {
    }

    /**
     * 默认 ZLM 应用名
     */
    public static final String DEFAULT_STREAM_APP = "live";

    /**
     * 默认播放协议
     */
    public static final String DEFAULT_PLAY_PROTOCOL = "flv";

    /**
     * ZLM 默认虚拟主机
     */
    public static final String DEFAULT_VHOST = "__defaultVhost__";

    /**
     * 流已关闭
     */
    public static final Integer STREAM_STATUS_CLOSED = 0;

    /**
     * 流在线
     */
    public static final Integer STREAM_STATUS_ONLINE = 1;

    /**
     * 流启动中
     */
    public static final Integer STREAM_STATUS_STARTING = 2;
}