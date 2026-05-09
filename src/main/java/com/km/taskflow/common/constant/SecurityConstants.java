package com.km.taskflow.common.constant;

/**
 * @author zzy
 */
public class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String[] PERMIT_ALL_URLS = {
            "/system/auth/login",
            "/test/**",
            "/doc.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
}