package com.sk.iba.module.alarm.service;

import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.alarm.dto.AlarmFalseAlarmBatchDTO;
import com.sk.iba.module.alarm.dto.AlarmFalseAlarmDTO;
import com.sk.iba.module.alarm.dto.AlarmRecordQueryDTO;
import com.sk.iba.module.alarm.vo.AlarmRecordVO;

import java.util.List;

/**
 * 告警记录 Service
 *
 * @author zzy
 */
public interface AlarmRecordService {

    /**
     * 分页查询告警
     */
    PageResult<AlarmRecordVO> pageAlarmRecords(AlarmRecordQueryDTO queryDTO);

    /**
     * 查询告警详情
     */
    AlarmRecordVO getAlarmRecordById(Long id);

    /**
     * 删除告警
     */
    void deleteAlarmRecord(Long id);

    /**
     * 标记误报
     */
    void markFalseAlarm(Long id, AlarmFalseAlarmDTO falseAlarmDTO);

    /**
     * 取消误报
     */
    void cancelFalseAlarm(Long id);

    /**
     * 批量删除告警
     */
    void deleteAlarmRecords(List<Long> ids);

    /**
     * 批量标记误报
     */
    void markFalseAlarms(AlarmFalseAlarmBatchDTO batchDTO);
}