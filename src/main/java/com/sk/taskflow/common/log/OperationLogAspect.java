package com.sk.taskflow.common.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.taskflow.common.constant.LogConstants;
import com.sk.taskflow.common.utils.IpUtils;
import com.sk.taskflow.security.SecurityUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 操作日志切面
 *
 * 只记录加了 @OperationLog 注解的方法。
 *
 * @author zzy
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final ObjectMapper objectMapper;

    /**
     * 敏感字段脱敏
     *
     * 会把 JSON 中 password、oldPassword、newPassword、token 等字段的值替换成 ******
     */
    private static final Pattern SENSITIVE_FIELD_PATTERN = Pattern.compile(
            "(\"(?:password|oldPassword|newPassword|token|authorization|secret)\"\\s*:\\s*\")(.*?)(\")",
            Pattern.CASE_INSENSITIVE
    );

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();

        HttpServletRequest request = getRequest();

        Long userId = getUserId();
        String username = getUsername();
        String ip = IpUtils.getClientIp(request);
        String requestMethod = request == null ? "" : request.getMethod();
        String requestUri = request == null ? "" : request.getRequestURI();
        String classMethod = getClassMethod(joinPoint);

        Object result = null;

        try {
            result = joinPoint.proceed();

            long costTime = System.currentTimeMillis() - startTime;

            log.info(
                    "[操作成功] module={}, operation={}, type={}, userId={}, username={}, ip={}, method={}, uri={}, classMethod={}, cost={}ms, params={}, result={}",
                    operationLog.module(),
                    operationLog.name(),
                    operationLog.type().getDescription(),
                    userId,
                    username,
                    ip,
                    requestMethod,
                    requestUri,
                    classMethod,
                    costTime,
                    operationLog.recordParams() ? buildParams(joinPoint) : LogConstants.NOT_RECORD,
                    operationLog.recordResult() ? toJson(result) : LogConstants.NOT_RECORD
            );
            
            return result;
        } catch (Throwable e) {
            long costTime = System.currentTimeMillis() - startTime;

            log.error(
                    "[操作失败] module={}, operation={}, type={}, userId={}, username={}, ip={}, method={}, uri={}, classMethod={}, cost={}ms, params={}, error={}",
                    operationLog.module(),
                    operationLog.name(),
                    operationLog.type().getDescription(),
                    userId,
                    username,
                    ip,
                    requestMethod,
                    requestUri,
                    classMethod,
                    costTime,
                    operationLog.recordParams() ? buildParams(joinPoint) : LogConstants.NOT_RECORD,
                    e.getMessage(),
                    e
            );

            throw e;
        }
    }

    /**
     * 获取当前请求
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        return attributes.getRequest();
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getUserId() {
        try {
            if (SecurityUtils.isLogin()) {
                return SecurityUtils.getUserId();
            }
        } catch (Exception ignored) {
            // 未登录或 Security 上下文为空时，不影响日志记录
        }

        return null;
    }

    /**
     * 获取当前登录用户名
     */
    private String getUsername() {
        try {
            if (SecurityUtils.isLogin()) {
                return SecurityUtils.getUsername();
            }
        } catch (Exception ignored) {
            // 未登录或 Security 上下文为空时，不影响日志记录
        }

        return LogConstants.ANONYMOUS_USER;
    }

    /**
     * 获取当前执行的类名和方法名
     */
    private String getClassMethod(ProceedingJoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringTypeName()
                + "."
                + joinPoint.getSignature().getName();
    }

    /**
     * 构建请求参数
     *
     * 会过滤掉 ServletRequest、ServletResponse、MultipartFile、BindingResult 等不可序列化或没必要记录的对象。
     */
    private String buildParams(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        Object[] filteredArgs = Arrays.stream(args)
                .filter(arg -> !(arg instanceof ServletRequest))
                .filter(arg -> !(arg instanceof ServletResponse))
                .filter(arg -> !(arg instanceof MultipartFile))
                .filter(arg -> !(arg instanceof MultipartFile[]))
                .filter(arg -> !(arg instanceof BindingResult))
                .toArray();

        return toJson(filteredArgs);
    }

    /**
     * 对象转 JSON
     */
    private String toJson(Object object) {
        if (object == null) {
            return "";
        }

        try {
            String json = objectMapper.writeValueAsString(object);
            return limitLength(maskSensitiveInfo(json));
        } catch (JsonProcessingException e) {
            return limitLength(maskSensitiveInfo(String.valueOf(object)));
        }
    }

    /**
     * 敏感信息脱敏
     */
    private String maskSensitiveInfo(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }

        return SENSITIVE_FIELD_PATTERN.matcher(content).replaceAll("$1******$3");
    }

    /**
     * 限制日志长度，避免参数或响应过大刷屏
     */
    private String limitLength(String value) {
        if (value == null) {
            return "";
        }

        if (value.length() <= LogConstants.MAX_LOG_LENGTH) {
            return value;
        }

        return value.substring(0, LogConstants.MAX_LOG_LENGTH) + "...";
    }
}