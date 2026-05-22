package com.sk.iba.common.utils;

import com.sk.iba.common.constant.LogConstants;
import jakarta.servlet.http.HttpServletRequest;

/**
 * IP 工具类
 *
 * @author zzy
 */
public class IpUtils {

    private IpUtils() {
    }

    /**
     * 获取客户端真实 IP
     *
     * 说明：
     * 1. 如果项目部署在 Nginx、网关后面，优先从 X-Forwarded-For / X-Real-IP 获取
     * 2. 如果没有代理请求头，则使用 request.getRemoteAddr()
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String xForwardedFor = request.getHeader(LogConstants.HEADER_X_FORWARDED_FOR);

        if (hasRealIp(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader(LogConstants.HEADER_X_REAL_IP);

        if (hasRealIp(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    private static boolean hasRealIp(String ip) {
        return ip != null
                && !ip.isBlank()
                && !LogConstants.UNKNOWN.equalsIgnoreCase(ip);
    }
}