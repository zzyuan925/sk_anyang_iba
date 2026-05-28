package com.sk.iba.common.constant;

/**
 * 海康平台常量
 *
 * @author zzy
 */
public class HikPlatformConstants {

    private HikPlatformConstants() {
    }

    /**
     * API网关后端服务上下文
     */
    public static final String ARTEMIS_PATH = "/artemis";

    /**
     * 平台协议：http / https
     */
    public static final String PROTOCOL = "https";

    /**
     * 平台网关地址，格式：IP:端口，不要带 http:// 或 https://
     */
    public static final String HOST = "127.0.0.1";

    /**
     * 合作方 Key
     */
    public static final String APP_KEY = "你的appKey";

    /**
     * 合作方 Secret
     */
    public static final String APP_SECRET = "你的appSecret";

    /**
     * JSON Content-Type
     */
    public static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * 连接超时时间
     */
    public static final int CONNECT_TIMEOUT = 10000;

    /**
     * 读取超时时间
     */
    public static final int SOCKET_TIMEOUT = 60000;
}