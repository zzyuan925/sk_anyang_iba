package com.sk.iba.module.device.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 摄像头功能ROI配置实体
 *
 * @author zzy
 */
@Data
@TableName("biz_camera_function_roi")
public class CameraFunctionRoi {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long cameraFunctionId;

    /**
     * ROI昵称
     */
    private String roiName;

    /**
     * ROI类型ID
     */
    private Long roiTypeId;

    /**
     * ROI多边形坐标JSON
     */
    private String roiData;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}