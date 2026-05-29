package com.sk.iba.module.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sk.iba.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 算法运行实体
 *
 * @author zzy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("biz_algorithm_runtime")
public class AlgorithmRuntime extends BaseEntity {

    /**
     * 算法服务器ID
     */
    private Long serverId;

    /**
     * 算法功能ID
     */
    private Long functionId;

    /**
     * 算法包ID
     */
    private Long algorithmPackageId;

    /**
     * 算法包部署路径
     */
    private String deployPath;

    /**
     * 运行状态：0未运行，1运行中，2运行异常
     */
    private Integer runStatus;
}