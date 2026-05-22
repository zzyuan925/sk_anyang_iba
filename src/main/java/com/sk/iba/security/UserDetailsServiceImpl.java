package com.sk.iba.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sk.iba.module.system.entity.SysUser;
import com.sk.iba.module.system.mapper.SysPermissionMapper;
import com.sk.iba.module.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security 用户加载服务
 *
 * @author zzy
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;

    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));

        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
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
}