package com.sk.iba.module.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户区域关联实体
 *
 * @author zzy
 */
@Data
@TableName("sys_user_region")
public class SysUserRegion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long regionId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}