package com.sk.iba.module.device.controller;

import com.sk.iba.common.log.OperationLog;
import com.sk.iba.common.log.OperationType;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.common.result.Result;
import com.sk.iba.module.device.dto.RoiTypeCreateDTO;
import com.sk.iba.module.device.dto.RoiTypeQueryDTO;
import com.sk.iba.module.device.dto.RoiTypeUpdateDTO;
import com.sk.iba.module.device.service.RoiTypeService;
import com.sk.iba.module.device.vo.RoiTypeOptionVO;
import com.sk.iba.module.device.vo.RoiTypeVO;
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
@Tag(name = "ROI类型管理模块", description = "ROI类型的增删改查及分页查询")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/device/roi-type")
public class RoiTypeController {

    private final RoiTypeService roiTypeService;

    @OperationLog(module = "ROI类型管理", name = "分页查询ROI类型", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "分页查询ROI类型")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('device:roiType:list')")
    public Result<PageResult<RoiTypeVO>> page(@ParameterObject @Validated RoiTypeQueryDTO queryDTO) {
        return Result.success(roiTypeService.pageRoiTypes(queryDTO));
    }

    @OperationLog(module = "ROI类型管理", name = "获取ROI类型详情", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "获取ROI类型详情")
    @Parameter(name = "id", description = "ROI类型ID", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('device:roiType:detail')")
    public Result<RoiTypeVO> getById(@PathVariable @NotNull(message = "ROI类型ID不能为空") Long id) {
        return Result.success(roiTypeService.getRoiTypeById(id));
    }

    @OperationLog(module = "ROI类型管理", name = "创建ROI类型", type = OperationType.CREATE)
    @Operation(summary = "创建ROI类型")
    @PostMapping
    @PreAuthorize("hasAuthority('device:roiType:create')")
    public Result<Long> create(@RequestBody @Valid RoiTypeCreateDTO createDTO) {
        return Result.success(roiTypeService.createRoiType(createDTO));
    }

    @OperationLog(module = "ROI类型管理", name = "修改ROI类型", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "修改ROI类型")
    @PutMapping
    @PreAuthorize("hasAuthority('device:roiType:update')")
    public Result<Void> update(@RequestBody @Valid RoiTypeUpdateDTO updateDTO) {
        roiTypeService.updateRoiType(updateDTO);
        return Result.success();
    }

    @OperationLog(module = "ROI类型管理", name = "删除ROI类型", type = OperationType.DELETE, recordResult = false)
    @Operation(summary = "逻辑删除ROI类型")
    @Parameter(name = "id", description = "ROI类型ID", required = true, example = "1")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('device:roiType:delete')")
    public Result<Void> delete(@PathVariable @NotNull(message = "ROI类型ID不能为空") Long id) {
        roiTypeService.deleteRoiType(id);
        return Result.success();
    }

    @OperationLog(module = "ROI类型管理", name = "查询启用ROI类型下拉选项", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "查询启用ROI类型下拉选项", description = "用于配置ROI时选择类型")
    @GetMapping("/options")
    @PreAuthorize("hasAuthority('device:roiType:options')")
    public Result<List<RoiTypeOptionVO>> options() {
        return Result.success(roiTypeService.listEnabledRoiTypeOptions());
    }
}