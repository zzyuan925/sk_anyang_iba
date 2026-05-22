package com.sk.iba.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 通用状态枚举
 *
 * @author zzy
 */
@Getter
public enum StatusEnum {

    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private final Integer code;

    private final String description;

    StatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 判断是否启用
     */
    public static boolean isEnabled(Integer code) {
        return ENABLED.code.equals(code);
    }

    /**
     * 判断是否禁用
     */
    public static boolean isDisabled(Integer code) {
        return DISABLED.code.equals(code);
    }

    /**
     * 判断状态是否合法
     */
    public static boolean isValid(Integer code) {
        if (code == null) {
            return false;
        }

        return Arrays.stream(values())
                .anyMatch(item -> item.code.equals(code));
    }
}