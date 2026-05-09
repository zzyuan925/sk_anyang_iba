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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);

        if (StringUtils.hasText(token) && jwtUtils.isTokenValid(token)) {
            Long userId = jwtUtils.getUserId(token);

            SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getId, userId));

            if (user != null && SystemConstants.STATUS_ENABLED.equals(user.getStatus())) {
                List<String> permissions = sysPermissionMapper.selectPermissionCodesByUserId(user.getId());

                LoginUser loginUser = new LoginUser(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getStatus(),
                        permissions
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
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