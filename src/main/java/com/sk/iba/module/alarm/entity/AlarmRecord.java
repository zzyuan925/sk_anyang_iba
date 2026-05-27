package com.sk.iba.module.alarm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 告警记录实体
 *
 * @author zzy
 */
@Data
@TableName("biz_alarm_record")
public class AlarmRecord {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 摄像头ID
     */
    private Long cameraId;

    /**
     * 摄像头名称快照
     */
    private String cameraName;

    /**
     * 摄像头编码快照
     */
    private String cameraCode;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 区域名称快照
     */
    private String regionName;

    /**
     * 区域编码快照
     */
    private String regionCode;

    /**
     * 算法功能ID
     */
    private Long functionId;

    /**
     * 算法功能名称快照
     */
    private String functionName;

    /**
     * 算法功能编码快照
     */
    private String functionCode;

    /**
     * 告警时间
     */
    private LocalDateTime alarmTime;

    /**
     * 告警抓拍图地址
     */
    private String imageUrl;

    /**
     * 告警视频地址
     */
    private String videoUrl;

    /**
     * 是否标记为误报：0未标记，1误报
     */
    private Integer isFalseAlarm;

    /**
     * 误报标记人ID
     */
    private Long falseAlarmBy;

    /**
     * 误报标记时间
     */
    private LocalDateTime falseAlarmTime;

    /**
     * 误报备注
     */
    private String falseAlarmRemark;

    /**
     * 逻辑删除：0未删除，1已删除
     */
    @TableLogic
    private Integer deleted;
}