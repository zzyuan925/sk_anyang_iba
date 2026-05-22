package com.sk.iba.common.constant;

/**
 * 日志相关常量
 *
 * 包含 traceId、请求头、操作日志默认值等。
 *
 * @author zzy
 */
public class LogConstants {

    private LogConstants() {
    }

    /**
     * MDC 中保存 traceId 的 key
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 请求头/响应头中的 traceId 名称
     */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * 匿名用户
     */
    public static final String ANONYMOUS_USER = "anonymous";

    /**
     * 未知值
     */
    public static final String UNKNOWN = "unknown";

    /**
     * 未记录
     */
    public static final String NOT_RECORD = "未记录";

    /**
     * 最大日志字段长度，避免请求参数或响应结果过长刷屏
     */
    public static final int MAX_LOG_LENGTH = 2000;

    /**
     * 代理转发 IP 请求头
     */
    public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";

    /**
     * Nginx 常用真实 IP 请求头
     */
    public static final String HEADER_X_REAL_IP = "X-Real-IP";
}