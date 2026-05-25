package com.sk.iba.security;

import com.sk.iba.common.constant.SystemConstants;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security 工具类
 *
 * @author zzy
 */
public class SecurityUtils {

    private SecurityUtils() {
    }

    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        return loginUser;
    }

    public static boolean isLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getPrincipal() instanceof LoginUser;
    }

    public static Long getUserId() {
        return getLoginUser().getUserId();
    }

    public static String getUsername() {
        return getLoginUser().getUsername();
    }

    public static boolean isSuperAdmin() {
        return SystemConstants.ADMIN_USER_ID.equals(getUserId());
    }
}