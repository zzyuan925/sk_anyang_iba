package com.sk.taskflow.common.log;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * 用在 Controller 方法上，用于记录用户操作日志。
 *
 * @author zzy
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 模块名称
     * 例如：用户管理、角色管理、权限管理
     */
    String module();

    /**
     * 操作名称
     * 例如：新增用户、修改角色、分配权限
     */
    String name();

    /**
     * 操作类型
     */
    OperationType type() default OperationType.OTHER;

    /**
     * 是否记录请求参数
     */
    boolean recordParams() default true;

    /**
     * 是否记录响应结果
     */
    boolean recordResult() default true;
}