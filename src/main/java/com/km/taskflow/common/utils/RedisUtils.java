package com.km.taskflow.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 *
 * 使用 StringRedisTemplate + ObjectMapper 存取对象。
 *
 * @author zzy
 */
@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    /**
     * 写入字符串
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取字符串
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 写入对象
     */
    public void setObject(String key, Object value, long timeout, TimeUnit unit) {
        if (value == null) {
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json, timeout, unit);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Redis 对象序列化失败", e);
        }
    }

    /**
     * 获取对象
     */
    public <T> T getObject(String key, Class<T> clazz) {
        String json = stringRedisTemplate.opsForValue().get(key);

        if (!StringUtils.hasText(json)) {
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Redis 对象反序列化失败", e);
        }
    }

    /**
     * 删除 key
     */
    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }
    
    /**
     * 批量删除 key
     */
    public Long delete(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0L;
        }
        return stringRedisTemplate.delete(keys);
    }
    
    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }
}