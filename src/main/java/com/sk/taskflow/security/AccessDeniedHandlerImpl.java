package com.sk.taskflow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.taskflow.common.result.Result;
import com.sk.taskflow.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 权限不足处理
 *
 * @author zzy
 */
@Component
@RequiredArgsConstructor
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        Result<Void> result = Result.fail(ResultCode.FORBIDDEN.getCode(), "没有权限");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}