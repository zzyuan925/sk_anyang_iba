package com.sk.iba.common.constant;

/**
 * 认证相关常量
 *
 * @author zzy
 */
public class AuthConstants {

    private AuthConstants() {
    }

    /**
     * 请求头名称
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Token 类型
     */
    public static final String TOKEN_TYPE_BEARER = "Bearer";

    /**
     * Token 前缀，注意后面有空格
     */
    public static final String TOKEN_PREFIX = TOKEN_TYPE_BEARER + " ";
}