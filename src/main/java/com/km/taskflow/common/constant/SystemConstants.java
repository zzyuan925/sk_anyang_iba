package com.km.taskflow.common.constant;

/**
 * 系统通用常量
 *
 * @author zzy
 */
public class SystemConstants {

    private SystemConstants() {
    }

    /**
     * 状态：禁用
     */
    public static final Integer STATUS_DISABLED = 0;

    /**
     * 状态：启用
     */
    public static final Integer STATUS_ENABLED = 1;

    /**
     * 树结构根节点父ID
     */
    public static final Long ROOT_PARENT_ID = 0L;

    /**
     * 系统内置管理员角色编码
     */
    public static final String ADMIN_ROLE_CODE = "admin";
}