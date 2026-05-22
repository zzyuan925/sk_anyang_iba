package com.sk.taskflow.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 权限类型枚举
 *
 * @author zzy
 */
@Getter
public enum PermissionTypeEnum {

    MENU(1, "菜单"),
    BUTTON(2, "按钮"),
    API(3, "接口");

    private final Integer code;

    private final String description;

    PermissionTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 判断权限类型是否合法
     */
    public static boolean isValid(Integer code) {
        if (code == null) {
            return false;
        }

        return Arrays.stream(values())
                .anyMatch(item -> item.code.equals(code));
    }

    /**
     * 判断是否是接口权限
     */
    public static boolean isApi(Integer code) {
        return API.code.equals(code);
    }
}