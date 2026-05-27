package com.sk.iba.module.device.controller;

import com.sk.iba.common.log.OperationLog;
import com.sk.iba.common.log.OperationType;
import com.sk.iba.common.result.Result;
import com.sk.iba.module.device.dto.CameraFunctionRoiSaveDTO;
import com.sk.iba.module.device.dto.CameraFunctionTimeSaveDTO;
import com.sk.iba.module.device.service.CameraFunctionConfigService;
import com.sk.iba.module.device.vo.CameraFunctionRoiVO;
import com.sk.iba.module.device.vo.CameraFunctionTimeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zzy
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/device/camera-function")
public class CameraFunctionConfigController {

    private final CameraFunctionConfigService cameraFunctionConfigService;

    @OperationLog(module = "摄像头功能配置", name = "查询ROI配置", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "查询ROI配置")
    @Parameter(name = "cameraFunctionId", description = "摄像头功能关联ID", required = true, example = "1")
    @GetMapping("/{cameraFunctionId}/roi")
    @PreAuthorize("hasAuthority('device:camera:configFunction')")
    public Result<CameraFunctionRoiVO> getRoi(@PathVariable @NotNull(message = "摄像头功能关联ID不能为空") Long cameraFunctionId) {
        return Result.success(cameraFunctionConfigService.getRoi(cameraFunctionId));
    }

    @OperationLog(module = "摄像头功能配置", name = "保存ROI配置", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "保存ROI配置", description = "保存ROI配置，覆盖原有ROI")
    @Parameter(name = "cameraFunctionId", description = "摄像头功能关联ID", required = true, example = "1")
    @PutMapping("/{cameraFunctionId}/roi")
    @PreAuthorize("hasAuthority('device:camera:configFunction')")
    public Result<Void> saveRoi(@PathVariable @NotNull(message = "摄像头功能关联ID不能为空") Long cameraFunctionId,
                                @RequestBody @Valid CameraFunctionRoiSaveDTO saveDTO) {
        cameraFunctionConfigService.saveRoi(cameraFunctionId, saveDTO);
        return Result.success();
    }

    @OperationLog(module = "摄像头功能配置", name = "清空ROI配置", type = OperationType.DELETE, recordResult = false)
    @Operation(summary = "清空ROI配置", description = "清空ROI后默认全屏")
    @Parameter(name = "cameraFunctionId", description = "摄像头功能关联ID", required = true, example = "1")
    @DeleteMapping("/{cameraFunctionId}/roi")
    @PreAuthorize("hasAuthority('device:camera:configFunction')")
    public Result<Void> clearRoi(@PathVariable @NotNull(message = "摄像头功能关联ID不能为空") Long cameraFunctionId) {
        cameraFunctionConfigService.clearRoi(cameraFunctionId);
        return Result.success();
    }

    @OperationLog(module = "摄像头功能配置", name = "查询运行时间段", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "查询运行时间段")
    @Parameter(name = "cameraFunctionId", description = "摄像头功能关联ID", required = true, example = "1")
    @GetMapping("/{cameraFunctionId}/times")
    @PreAuthorize("hasAuthority('device:camera:configFunction')")
    public Result<List<CameraFunctionTimeVO>> listTimes(@PathVariable @NotNull(message = "摄像头功能关联ID不能为空") Long cameraFunctionId) {
        return Result.success(cameraFunctionConfigService.listTimes(cameraFunctionId));
    }

    @OperationLog(module = "摄像头功能配置", name = "保存运行时间段", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "保存运行时间段", description = "保存运行时间段，覆盖原有时间段；空列表表示全天运行")
    @Parameter(name = "cameraFunctionId", description = "摄像头功能关联ID", required = true, example = "1")
    @PutMapping("/{cameraFunctionId}/times")
    @PreAuthorize("hasAuthority('device:camera:configFunction')")
    public Result<Void> saveTimes(@PathVariable @NotNull(message = "摄像头功能关联ID不能为空") Long cameraFunctionId,
                                  @RequestBody @Valid CameraFunctionTimeSaveDTO saveDTO) {
        cameraFunctionConfigService.saveTimes(cameraFunctionId, saveDTO);
        return Result.success();
    }

    @OperationLog(module = "摄像头功能配置", name = "清空运行时间段", type = OperationType.DELETE, recordResult = false)
    @Operation(summary = "清空运行时间段", description = "清空后默认全天运行")
    @Parameter(name = "cameraFunctionId", description = "摄像头功能关联ID", required = true, example = "1")
    @DeleteMapping("/{cameraFunctionId}/times")
    @PreAuthorize("hasAuthority('device:camera:configFunction')")
    public Result<Void> clearTimes(@PathVariable @NotNull(message = "摄像头功能关联ID不能为空") Long cameraFunctionId) {
        cameraFunctionConfigService.clearTimes(cameraFunctionId);
        return Result.success();
    }
}