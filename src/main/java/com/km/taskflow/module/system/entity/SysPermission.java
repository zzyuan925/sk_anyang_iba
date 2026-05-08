package com.km.taskflow.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统权限实体
 *
 * @author zzy
 */
@Data
@TableName("sys_permission")
public class SysPermission {

    @TableId(type = IdType.AUTO)
    private Long id;

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

    /**
     * 逻辑删除：0未删除，1已删除
     */
    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}