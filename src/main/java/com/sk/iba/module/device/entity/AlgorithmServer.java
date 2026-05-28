package com.sk.iba.module.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sk.iba.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 算法服务器实体
 *
 * @author zzy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("biz_algorithm_server")
public class AlgorithmServer extends BaseEntity {

    /**
     * 服务器名
     */
    private String serverName;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 服务器IP
     */
    private String ip;

    /**
     * 服务器端口
     */
    private Integer port;

    /**
     * 部署地址/部署目录
     */
    private String deployPath;

    /**
     * 状态：0禁用，1启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}