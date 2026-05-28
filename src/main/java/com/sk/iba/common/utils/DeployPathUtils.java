package com.sk.iba.common.utils;

import org.springframework.util.StringUtils;

/**
 * 部署路径工具类
 *
 * 兼容 Ubuntu/Linux 和 Windows：
 * 1. Ubuntu/Linux: /opt/algorithm
 * 2. Windows: D:/algorithm 或 D:\\algorithm
 * 3. UNC: //192.168.1.10/share/algorithm
 *
 * @author zzy
 */
public class DeployPathUtils {

    private DeployPathUtils() {
    }

    /**
     * 规范化部署路径。
     *
     * 数据库里统一存储为 / 分隔，避免 Windows 反斜杠转义问题。
     */
    public static String normalize(String deployPath) {
        if (!StringUtils.hasText(deployPath)) {
            return deployPath;
        }

        String path = deployPath.trim().replace("\\", "/");

        while (path.contains("//") && !path.startsWith("//")) {
            path = path.replace("//", "/");
        }

        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }

    /**
     * 判断是否是合法的部署目录格式。
     */
    public static boolean isValidDeployPath(String deployPath) {
        if (!StringUtils.hasText(deployPath)) {
            return false;
        }

        String path = normalize(deployPath);

        // Linux 绝对路径：/opt/algorithm
        if (path.startsWith("/")) {
            return true;
        }

        // Windows 盘符路径：D:/algorithm
        if (path.matches("^[a-zA-Z]:/.+")) {
            return true;
        }

        // Windows UNC / 网络共享路径：//server/share/path
        return path.startsWith("//") && path.length() > 2;
    }
}