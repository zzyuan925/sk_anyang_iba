package com.sk.taskflow.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sk.taskflow.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户实体
 *
 * @author zzy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private String username;

    private String password;

    private String realName;

    private String email;

    private String phone;

    /**
     * 状态：0禁用，1启用
     */
    private Integer status;
}