package com.km.taskflow.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.km.taskflow.common.constant.AuthConstants;
import com.km.taskflow.common.constant.SystemConstants;
import com.km.taskflow.module.system.entity.SysUser;
import com.km.taskflow.module.system.mapper.SysPermissionMapper;
import com.km.taskflow.module.system.mapper.SysUserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器
 *
 * @author zzy
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final SysUserMapper sysUserMapper;

    private final SysPermissionMapper sysPermissionMapper;

    private final LoginUserCacheService loginUserCacheService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);

        if (!StringUtils.hasText(token) || !jwtUtils.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtUtils.getUserId(token);

        LoginUser loginUser = loginUserCacheService.getLoginUser(userId);

        if (loginUser == null) {
            loginUser = buildLoginUser(userId);

            if (loginUser != null) {
                loginUserCacheService.setLoginUser(loginUser);
            }
        }

        if (loginUser != null && SystemConstants.STATUS_ENABLED.equals(loginUser.getStatus())) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Redis 没有登录用户缓存时，从数据库重建 LoginUser。
     */
    private LoginUser buildLoginUser(Long userId) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getId, userId));

        if (user == null || !SystemConstants.STATUS_ENABLED.equals(user.getStatus())) {
            return null;
        }

        List<String> permissions = sysPermissionMapper.selectPermissionCodesByUserId(user.getId());

        return new LoginUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getStatus(),
                permissions
        );
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(authorization)
                || !authorization.startsWith(AuthConstants.TOKEN_PREFIX)) {
            return null;
        }

        return authorization.substring(AuthConstants.TOKEN_PREFIX.length());
    }
}