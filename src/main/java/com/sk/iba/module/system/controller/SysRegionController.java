package com.sk.iba.module.system.controller;

import com.sk.iba.common.log.OperationLog;
import com.sk.iba.common.log.OperationType;
import com.sk.iba.common.result.Result;
import com.sk.iba.module.system.dto.RegionCreateDTO;
import com.sk.iba.module.system.dto.RegionQueryDTO;
import com.sk.iba.module.system.dto.RegionUpdateDTO;
import com.sk.iba.module.system.service.SysRegionService;
import com.sk.iba.module.system.vo.RegionOptionVO;
import com.sk.iba.module.system.vo.RegionTreeVO;
import com.sk.iba.module.system.vo.RegionVO;
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
@Tag(name = "区域管理模块", description = "区域的增删改查及树形查询")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/region")
public class SysRegionController {

    private final SysRegionService sysRegionService;

    @OperationLog(module = "区域管理", name = "树形查询区域", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "树形查询区域", description = "根据区域名称、区域编码、状态查询区域树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:region:list')")
    public Result<List<RegionTreeVO>> tree(@ParameterObject @Validated RegionQueryDTO queryDTO) {
        return Result.success(sysRegionService.treeRegions(queryDTO));
    }

    @OperationLog(module = "区域管理", name = "获取区域详情", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "获取区域详情")
    @Parameter(name = "id", description = "区域ID", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:region:detail')")
    public Result<RegionVO> getById(@PathVariable @NotNull(message = "区域ID不能为空") Long id) {
        return Result.success(sysRegionService.getRegionById(id));
    }

    @OperationLog(module = "区域管理", name = "创建区域", type = OperationType.CREATE)
    @Operation(summary = "创建区域", description = "新增区域，成功后返回主键ID")
    @PostMapping
    @PreAuthorize("hasAuthority('system:region:create')")
    public Result<Long> create(@RequestBody @Valid RegionCreateDTO createDTO) {
        return Result.success(sysRegionService.createRegion(createDTO));
    }

    @OperationLog(module = "区域管理", name = "修改区域", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "修改区域", description = "根据 ID 修改区域信息")
    @PutMapping
    @PreAuthorize("hasAuthority('system:region:update')")
    public Result<Void> update(@RequestBody @Valid RegionUpdateDTO updateDTO) {
        sysRegionService.updateRegion(updateDTO);
        return Result.success();
    }

    @OperationLog(module = "区域管理", name = "删除区域", type = OperationType.DELETE, recordResult = false)
    @Operation(summary = "逻辑删除区域", description = "根据 ID 逻辑删除区域")
    @Parameter(name = "id", description = "区域ID", required = true, example = "1")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:region:delete')")
    public Result<Void> delete(@PathVariable @NotNull(message = "区域ID不能为空") Long id) {
        sysRegionService.deleteRegion(id);
        return Result.success();
    }

    @OperationLog(module = "区域管理", name = "查询启用区域下拉选项", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "查询启用区域下拉选项", description = "用于用户分配区域、摄像头选择区域")
    @GetMapping("/options")
    @PreAuthorize("hasAuthority('system:region:options')")
    public Result<List<RegionOptionVO>> options() {
        return Result.success(sysRegionService.listEnabledRegionOptions());
    }
}