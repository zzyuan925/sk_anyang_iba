package com.sk.iba.module.device.controller;

import com.sk.iba.common.log.OperationLog;
import com.sk.iba.common.log.OperationType;
import com.sk.iba.common.result.Result;
import com.sk.iba.module.device.dto.CameraDirectProbeDTO;
import com.sk.iba.module.device.service.CameraAccessService;
import com.sk.iba.module.device.vo.CameraDirectProbeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 摄像头接入 Controller
 *
 * @author zzy
 */
@Tag(name = "摄像头接入模块", description = "摄像头直连识别、平台搜索、平台取流")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/device/camera")
public class CameraAccessController {

    private final CameraAccessService cameraAccessService;

    @OperationLog(module = "摄像头接入", name = "直连摄像头识别", type = OperationType.QUERY, recordParams = false, recordResult = false)
    @Operation(summary = "直连摄像头识别", description = "根据 IP、RTSP端口、账号、密码调用海康 ISAPI 获取设备信息和码流地址")
    @PostMapping("/direct/probe")
    @PreAuthorize("hasAuthority('device:camera:create')")
    public Result<CameraDirectProbeVO> directProbe(@RequestBody @Valid CameraDirectProbeDTO probeDTO) {
        return Result.success(cameraAccessService.directProbe(probeDTO));
    }
}