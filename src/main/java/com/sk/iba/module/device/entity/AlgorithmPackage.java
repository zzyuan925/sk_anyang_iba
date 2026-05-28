package com.sk.iba.module.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sk.iba.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 算法包实体
 *
 * @author zzy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("biz_algorithm_package")
public class AlgorithmPackage extends BaseEntity {

    /**
     * 算法功能ID
     */
    private Long functionId;

    /**
     * 算法包路径
     */
    private String packagePath;

    /**
     * 算法包描述
     */
    private String description;

    /**
     * 算法包版本
     */
    private String version;

    /**
     * 启动环境
     */
    private String runtimeEnv;

    /**
     * 启动文件名
     */
    private String startFileName;

    /**
     * 权重文件路径
     */
    private String weightPath;
}