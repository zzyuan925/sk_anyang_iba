package com.sk.iba.module.alarm.dto;

import com.sk.iba.common.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 告警记录分页查询参数
 *
 * @author zzy
 */
@Schema(description = "告警记录分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class AlarmRecordQueryDTO extends PageQuery {

    @Schema(description = "区域ID", example = "1")
    private Long regionId;

    @Schema(description = "摄像头ID", example = "1")
    private Long cameraId;

    @Schema(description = "算法功能ID", example = "1")
    private Long functionId;

    @Schema(description = "摄像头名称", example = "入口")
    private String cameraName;

    @Schema(description = "摄像头编码", example = "camera_001")
    private String cameraCode;

    @Schema(description = "是否误报：0未标记，1误报", example = "0")
    private Integer isFalseAlarm;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}