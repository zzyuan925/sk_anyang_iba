package com.sk.iba.module.device.controller;

import com.sk.iba.common.log.OperationLog;
import com.sk.iba.common.log.OperationType;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.common.result.Result;
import com.sk.iba.module.device.dto.CameraAssignFunctionDTO;
import com.sk.iba.module.device.dto.CameraCreateDTO;
import com.sk.iba.module.device.dto.CameraQueryDTO;
import com.sk.iba.module.device.dto.CameraUpdateDTO;
import com.sk.iba.module.device.service.CameraService;
import com.sk.iba.module.device.vo.CameraFunctionVO;
import com.sk.iba.module.device.vo.CameraOptionVO;
import com.sk.iba.module.device.vo.CameraVO;
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

import java.util.List;

/**
 * @author zzy
 */
@Tag(name = "摄像头管理模块", description = "摄像头的增删改查及分页查询")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/device/camera")
public class CameraController {

    private final CameraService cameraService;

    @OperationLog(module = "摄像头管理", name = "分页查询摄像头", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "分页查询摄像头", description = "根据摄像头名称、编码、区域、视频源类型、状态进行分页查询")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('device:camera:list')")
    public Result<PageResult<CameraVO>> page(@ParameterObject @Validated CameraQueryDTO queryDTO) {
        return Result.success(cameraService.pageCameras(queryDTO));
    }

    @OperationLog(module = "摄像头管理", name = "获取摄像头详情", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "获取摄像头详情")
    @Parameter(name = "id", description = "摄像头ID", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('device:camera:detail')")
    public Result<CameraVO> getById(@PathVariable @NotNull(message = "摄像头ID不能为空") Long id) {
        return Result.success(cameraService.getCameraById(id));
    }

    @OperationLog(module = "摄像头管理", name = "创建摄像头", type = OperationType.CREATE)
    @Operation(summary = "创建摄像头", description = "新增摄像头，成功后返回主键ID")
    @PostMapping
    @PreAuthorize("hasAuthority('device:camera:create')")
    public Result<Long> create(@RequestBody @Valid CameraCreateDTO createDTO) {
        return Result.success(cameraService.createCamera(createDTO));
    }

    @OperationLog(module = "摄像头管理", name = "修改摄像头", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "修改摄像头", description = "根据 ID 修改摄像头信息")
    @PutMapping
    @PreAuthorize("hasAuthority('device:camera:update')")
    public Result<Void> update(@RequestBody @Valid CameraUpdateDTO updateDTO) {
        cameraService.updateCamera(updateDTO);
        return Result.success();
    }

    @OperationLog(module = "摄像头管理", name = "删除摄像头", type = OperationType.DELETE, recordResult = false)
    @Operation(summary = "逻辑删除摄像头", description = "根据 ID 逻辑删除摄像头")
    @Parameter(name = "id", description = "摄像头ID", required = true, example = "1")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('device:camera:delete')")
    public Result<Void> delete(@PathVariable @NotNull(message = "摄像头ID不能为空") Long id) {
        cameraService.deleteCamera(id);
        return Result.success();
    }

    @OperationLog(module = "摄像头管理", name = "查询启用摄像头下拉选项", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "查询启用摄像头下拉选项", description = "用于算法配置、告警查询等选择摄像头")
    @GetMapping("/options")
    @PreAuthorize("hasAuthority('device:camera:options')")
    public Result<List<CameraOptionVO>> options() {
        return Result.success(cameraService.listEnabledCameraOptions());
    }

    @OperationLog(module = "摄像头管理", name = "查询摄像头已绑定功能", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "查询摄像头已绑定功能", description = "根据摄像头ID查询该摄像头已绑定的算法功能列表")
    @Parameter(name = "cameraId", description = "摄像头ID", required = true, example = "1")
    @GetMapping("/{cameraId}/functions")
    @PreAuthorize("hasAuthority('device:camera:listFunctions')")
    public Result<List<CameraFunctionVO>> listCameraFunctions(@PathVariable @NotNull(message = "摄像头ID不能为空") Long cameraId) {
        return Result.success(cameraService.listCameraFunctions(cameraId));
    }

    @OperationLog(module = "摄像头管理", name = "给摄像头分配功能", type = OperationType.ASSIGN, recordResult = false)
    @Operation(summary = "给摄像头分配功能", description = "重新分配摄像头功能，会覆盖原有功能")
    @Parameter(name = "cameraId", description = "摄像头ID", required = true, example = "1")
    @PutMapping("/{cameraId}/functions")
    @PreAuthorize("hasAuthority('device:camera:assignFunction')")
    public Result<Void> assignFunctions(@PathVariable @NotNull(message = "摄像头ID不能为空") Long cameraId,
                                        @RequestBody @Valid CameraAssignFunctionDTO assignFunctionDTO) {
        assignFunctionDTO.setCameraId(cameraId);
        cameraService.assignFunctions(assignFunctionDTO);
        return Result.success();
    }
}