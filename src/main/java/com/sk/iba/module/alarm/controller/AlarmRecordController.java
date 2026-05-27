package com.sk.iba.module.alarm.controller;

import com.sk.iba.common.log.OperationLog;
import com.sk.iba.common.log.OperationType;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.common.result.Result;
import com.sk.iba.module.alarm.dto.AlarmFalseAlarmDTO;
import com.sk.iba.module.alarm.dto.AlarmRecordQueryDTO;
import com.sk.iba.module.alarm.service.AlarmRecordService;
import com.sk.iba.module.alarm.vo.AlarmRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 告警记录管理
 *
 * @author zzy
 */
@Tag(name = "告警记录模块", description = "告警记录查询、删除、误报标记、算法上报")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm/record")
public class AlarmRecordController {

    private final AlarmRecordService alarmRecordService;

    @OperationLog(module = "告警记录", name = "分页查询告警", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "分页查询告警", description = "根据区域、摄像头、算法功能、误报状态、时间范围分页查询告警")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('alarm:record:list')")
    public Result<PageResult<AlarmRecordVO>> page(@ParameterObject @Validated AlarmRecordQueryDTO queryDTO) {
        return Result.success(alarmRecordService.pageAlarmRecords(queryDTO));
    }

    @OperationLog(module = "告警记录", name = "获取告警详情", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "获取告警详情")
    @Parameter(name = "id", description = "告警ID", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('alarm:record:detail')")
    public Result<AlarmRecordVO> getById(@PathVariable @NotNull(message = "告警ID不能为空") Long id) {
        return Result.success(alarmRecordService.getAlarmRecordById(id));
    }

    @OperationLog(module = "告警记录", name = "删除告警", type = OperationType.DELETE, recordResult = false)
    @Operation(summary = "逻辑删除告警")
    @Parameter(name = "id", description = "告警ID", required = true, example = "1")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('alarm:record:delete')")
    public Result<Void> delete(@PathVariable @NotNull(message = "告警ID不能为空") Long id) {
        alarmRecordService.deleteAlarmRecord(id);
        return Result.success();
    }

    @OperationLog(module = "告警记录", name = "标记误报", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "标记误报")
    @Parameter(name = "id", description = "告警ID", required = true, example = "1")
    @PutMapping("/{id}/false-alarm")
    @PreAuthorize("hasAuthority('alarm:record:falseAlarm')")
    public Result<Void> markFalseAlarm(@PathVariable @NotNull(message = "告警ID不能为空") Long id,
                                       @RequestBody @Valid AlarmFalseAlarmDTO falseAlarmDTO) {
        alarmRecordService.markFalseAlarm(id, falseAlarmDTO);
        return Result.success();
    }

    @OperationLog(module = "告警记录", name = "取消误报", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "取消误报")
    @Parameter(name = "id", description = "告警ID", required = true, example = "1")
    @PutMapping("/{id}/false-alarm/cancel")
    @PreAuthorize("hasAuthority('alarm:record:falseAlarm')")
    public Result<Void> cancelFalseAlarm(@PathVariable @NotNull(message = "告警ID不能为空") Long id) {
        alarmRecordService.cancelFalseAlarm(id);
        return Result.success();
    }
}