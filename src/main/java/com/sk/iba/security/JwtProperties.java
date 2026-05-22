package com.sk.iba.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 *
 * @author zzy
 */
@Data
@Component
@ConfigurationProperties(prefix = "iba.jwt")
public class JwtProperties {

    /**
     * JWT 签名密钥
     */
    private String secret;

    /**
     * 过期时间，单位：分钟
     */
    private Long expireMinutes;
}