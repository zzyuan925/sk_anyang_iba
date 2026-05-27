package com.sk.iba.module.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sk.iba.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 算法功能实体
 *
 * @author zzy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("biz_function")
public class AlgorithmFunction extends BaseEntity {

    private String functionName;

    private String functionCode;

    /**
     * 状态：0禁用，1启用
     */
    private Integer status;

    private String remark;
}