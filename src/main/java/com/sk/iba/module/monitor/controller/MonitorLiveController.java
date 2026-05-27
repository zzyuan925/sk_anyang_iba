package com.sk.iba.module.monitor.controller;

import com.sk.iba.common.log.OperationLog;
import com.sk.iba.common.log.OperationType;
import com.sk.iba.common.result.Result;
import com.sk.iba.module.monitor.dto.ZlmStreamChangedDTO;
import com.sk.iba.module.monitor.service.MonitorLiveService;
import com.sk.iba.module.monitor.vo.CameraLivePlayVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 实时监控直播
 *
 * @author zzy
 */
@Tag(name = "实时监控直播模块", description = "摄像头直播播放和ZLM回调")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/monitor/live")
public class MonitorLiveController {

    private final MonitorLiveService monitorLiveService;

    @OperationLog(module = "实时监控", name = "获取摄像头直播地址", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "获取摄像头直播地址")
    @Parameter(name = "cameraId", description = "摄像头ID", required = true, example = "1")
    @GetMapping("/camera/{cameraId}")
    @PreAuthorize("hasAuthority('monitor:live:play')")
    public Result<CameraLivePlayVO> playCamera(@PathVariable @NotNull(message = "摄像头ID不能为空") Long cameraId) {
        return Result.success(monitorLiveService.playCamera(cameraId));
    }

    @Operation(summary = "ZLM流状态变化回调")
    @PostMapping("/zlm/on-stream-changed")
    public Map<String, Object> onStreamChanged(@RequestBody ZlmStreamChangedDTO dto) {
        monitorLiveService.handleStreamChanged(dto);
        return Map.of(
                "code", 0,
                "msg", "success"
        );
    }
}