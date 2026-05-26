package com.sk.iba.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sk.iba.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 区域实体
 *
 * @author zzy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_region")
public class SysRegion extends BaseEntity {

    /**
     * 父级区域ID，0表示根节点
     */
    private Long parentId;

    private String regionName;

    private String regionCode;

    private Integer sort;

    /**
     * 状态：0禁用，1启用
     */
    private Integer status;

    private String remark;
}