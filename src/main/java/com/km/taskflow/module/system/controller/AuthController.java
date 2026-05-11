package com.km.taskflow.module.system.controller;

import com.km.taskflow.common.result.Result;
import com.km.taskflow.common.result.ResultCode;
import com.km.taskflow.module.system.dto.LoginDTO;
import com.km.taskflow.module.system.vo.CurrentUserVO;
import com.km.taskflow.module.system.vo.LoginVO;
import com.km.taskflow.security.JwtUtils;
import com.km.taskflow.security.LoginUser;
import com.km.taskflow.security.LoginUserCacheService;
import com.km.taskflow.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证接口
 *
 * @author zzy
 */
@Tag(name = "认证模块", description = "登录、退出、当前用户等认证接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final LoginUserCacheService loginUserCacheService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO loginDTO) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

            LoginUser loginUser = (LoginUser) authenticationManager.authenticate(authenticationToken).getPrincipal();

            String token = jwtUtils.generateToken(loginUser);

            // 缓存登录用户信息
            loginUserCacheService.setLoginUser(loginUser);

            LoginVO loginVO = new LoginVO();
            loginVO.setToken(token);

            return Result.success(loginVO);
        } catch (DisabledException e) {
            return Result.fail(ResultCode.UNAUTHORIZED.getCode(), "用户已被禁用");
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return Result.fail(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        } catch (AuthenticationException e) {
            return Result.fail(ResultCode.UNAUTHORIZED.getCode(), "认证失败，请重试");
        }
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/me")
    public Result<CurrentUserVO> me() {
        LoginUser loginUser = SecurityUtils.getLoginUser();

        CurrentUserVO vo = new CurrentUserVO();
        vo.setUserId(loginUser.getUserId());
        vo.setUsername(loginUser.getUsername());
        vo.setPermissions(loginUser.getPermissions());

        return Result.success(vo);
    }

    @Operation(summary = "获取当前用户权限编码")
    @GetMapping("/permissions")
    public Result<List<String>> permissions() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return Result.success(loginUser.getPermissions());
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        if (SecurityUtils.isLogin()) {
            loginUserCacheService.deleteLoginUser(SecurityUtils.getUserId());
        }
        return Result.success();
    }
}