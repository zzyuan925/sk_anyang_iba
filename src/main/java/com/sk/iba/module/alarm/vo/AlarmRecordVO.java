package com.sk.iba.module.alarm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 告警记录 VO
 *
 * @author zzy
 */
@Schema(description = "告警记录VO")
@Data
public class AlarmRecordVO {

    @Schema(description = "告警ID", example = "1")
    private Long id;

    @Schema(description = "摄像头ID", example = "1")
    private Long cameraId;

    @Schema(description = "摄像头名称快照", example = "入口摄像头")
    private String cameraName;

    @Schema(description = "摄像头编码快照", example = "camera_001")
    private String cameraCode;

    @Schema(description = "区域ID", example = "1")
    private Long regionId;

    @Schema(description = "区域名称快照", example = "一楼大厅")
    private String regionName;

    @Schema(description = "区域编码快照", example = "region_001")
    private String regionCode;

    @Schema(description = "算法功能ID", example = "1")
    private Long functionId;

    @Schema(description = "算法功能名称快照", example = "人员聚集")
    private String functionName;

    @Schema(description = "算法功能编码快照", example = "crowd_detect")
    private String functionCode;

    @Schema(description = "告警时间")
    private LocalDateTime alarmTime;

    @Schema(description = "告警抓拍图地址")
    private String imageUrl;

    @Schema(description = "告警视频地址")
    private String videoUrl;

    @Schema(description = "是否标记为误报：0未标记，1误报", example = "0")
    private Integer isFalseAlarm;

    @Schema(description = "误报标记人ID", example = "1")
    private Long falseAlarmBy;

    @Schema(description = "误报标记时间")
    private LocalDateTime falseAlarmTime;

    @Schema(description = "误报备注")
    private String falseAlarmRemark;
}