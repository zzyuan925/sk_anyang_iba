package com.sk.iba.module.device.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 摄像头功能运行时间段实体
 *
 * @author zzy
 */
@Data
@TableName("biz_camera_function_time")
public class CameraFunctionTime {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long cameraFunctionId;

    private LocalTime startTime;

    private LocalTime endTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}