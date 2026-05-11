package com.km.taskflow.config;

import com.km.taskflow.common.constant.AuthConstants;
import com.km.taskflow.common.constant.LogConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * 跨域配置
 *
 * 注意：
 * 项目使用了 Spring Security，所以跨域配置需要交给 Security 使用。
 *
 * @author zzy
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        /*
         * 开发阶段可以先放开。
         * 如果 allowCredentials = true，不建议使用 allowedOrigins("*")，
         * 应该使用 allowedOriginPatterns。
         */
        configuration.addAllowedOriginPattern("*");

        /*
         * 允许携带认证信息。
         * 前端如果需要携带 Authorization 请求头，通常这里可以开启。
         */
        configuration.setAllowCredentials(true);

        /*
         * 允许的请求方法。
         */
        configuration.addAllowedMethod("*");

        /*
         * 允许的请求头。
         * 例如 Authorization、Content-Type、X-Trace-Id。
         */
        configuration.addAllowedHeader("*");

        /*
         * 允许前端读取的响应头。
         * 这里把 X-Trace-Id 暴露出去，方便前端在 Network 或响应拦截器里拿到。
         */
        configuration.addExposedHeader(LogConstants.TRACE_ID_HEADER);
        configuration.addExposedHeader(AuthConstants.AUTHORIZATION_HEADER);

        /*
         * 预检请求缓存时间，单位秒。
         */
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        /*
         * 所有接口都使用这套跨域规则。
         */
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}