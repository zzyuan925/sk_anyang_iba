package com.sk.iba.module.device.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 摄像头功能关联实体
 *
 * @author zzy
 */
@Data
@TableName("biz_camera_function")
public class CameraFunction {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long cameraId;

    private Long functionId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}