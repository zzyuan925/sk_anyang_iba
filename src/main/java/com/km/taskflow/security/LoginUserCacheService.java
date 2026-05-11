package com.km.taskflow.security;

import com.km.taskflow.common.constant.RedisConstants;
import com.km.taskflow.common.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 登录用户缓存服务
 *
 * @author zzy
 */
@Service
@RequiredArgsConstructor
public class LoginUserCacheService {

    private final RedisUtils redisUtils;

    private final JwtProperties jwtProperties;

    /**
     * 缓存登录用户
     */
    public void setLoginUser(LoginUser loginUser) {
        if (loginUser == null || loginUser.getUserId() == null) {
            return;
        }

        // Redis 登录态不需要保存密码 hash
        loginUser.setPassword(null);

        redisUtils.setObject(
                buildLoginUserKey(loginUser.getUserId()),
                loginUser,
                jwtProperties.getExpireMinutes(),
                TimeUnit.MINUTES
        );
    }

    /**
     * 获取登录用户
     */
    public LoginUser getLoginUser(Long userId) {
        if (userId == null) {
            return null;
        }

        return redisUtils.getObject(buildLoginUserKey(userId), LoginUser.class);
    }

    /**
     * 删除登录用户缓存
     */
    public void deleteLoginUser(Long userId) {
        if (userId == null) {
            return;
        }

        redisUtils.delete(buildLoginUserKey(userId));
    }

    /**
     * 刷新登录用户缓存时间
     *
     * 每次请求是否刷新过期时间，看项目需求。
     * 如果希望 token 固定过期，就不要调用这个方法。
     */
    public void refreshLoginUser(Long userId) {
        if (userId == null) {
            return;
        }

        redisUtils.expire(
                buildLoginUserKey(userId),
                jwtProperties.getExpireMinutes(),
                TimeUnit.MINUTES
        );
    }

    /**
     * 批量删除登录用户缓存
     */
    public void deleteLoginUsers(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        List<String> keys = userIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(this::buildLoginUserKey)
                .toList();

        redisUtils.delete(keys);
    }

    private String buildLoginUserKey(Long userId) {
        return RedisConstants.LOGIN_USER_KEY_PREFIX + userId;
    }
}