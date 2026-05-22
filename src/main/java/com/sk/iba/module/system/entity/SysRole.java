package com.sk.iba.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sk.iba.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色实体
 *
 * @author zzy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_role")
public class SysRole extends BaseEntity {

    private String roleName;

    private String roleCode;

    private String description;

    /**
     * 状态：0禁用，1启用
     */
    private Integer status;
}