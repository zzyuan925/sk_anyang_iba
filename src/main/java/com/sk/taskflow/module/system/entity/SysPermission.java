package com.sk.taskflow.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sk.taskflow.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统权限实体
 *
 * @author zzy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

    private String permissionName;

    private String permissionCode;

    /**
     * 权限类型：1菜单，2按钮，3接口
     */
    private Integer permissionType;

    private Long parentId;

    private String path;

    private String description;

    /**
     * 状态：0禁用，1启用
     */
    private Integer status;
}