package com.km.taskflow.common.log;

import lombok.Getter;

/**
 * 操作类型
 *
 * @author zzy
 */
@Getter
public enum OperationType {

    OTHER("其他"),
    QUERY("查询"),
    CREATE("新增"),
    UPDATE("修改"),
    DELETE("删除"),
    LOGIN("登录"),
    LOGOUT("退出"),
    ASSIGN("分配"),
    RESET_PASSWORD("重置密码"),
    CHANGE_PASSWORD("修改密码");

    private final String description;

    OperationType(String description) {
        this.description = description;
    }
}