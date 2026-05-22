package com.sk.iba.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sk.iba.common.enums.StatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 当前登录用户
 *
 * @author zzy
 */
@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {

    private Long userId;

    private String username;

    @JsonIgnore
    private String password;

    /**
     * 用户状态：0禁用，1启用
     */
    private Integer status;

    /**
     * 权限编码列表
     */
    private List<String> permissions;

    public LoginUser(Long userId, String username, String password, Integer status, List<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.status = status;
        this.permissions = permissions;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions == null ? List.of() : permissions.stream()
                .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                .toList();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return StatusEnum.isEnabled(status);
    }
}