package com.sk.iba.module.monitor.controller;

import com.sk.iba.common.log.OperationLog;
import com.sk.iba.common.log.OperationType;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.common.result.Result;
import com.sk.iba.module.monitor.dto.MediaServerCreateDTO;
import com.sk.iba.module.monitor.dto.MediaServerQueryDTO;
import com.sk.iba.module.monitor.dto.MediaServerUpdateDTO;
import com.sk.iba.module.monitor.service.MediaServerService;
import com.sk.iba.module.monitor.vo.MediaServerVO;
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
 * 默认只有一个，加增删改查接口是为了方便修改，如果启用了多个，那么直播getDefaultMediaServer()中需要修改一下逻辑(.last("LIMIT 1"))。
 * 流媒体服务器管理
 *
 * @author zzy
 */
@Tag(name = "流媒体服务器模块", description = "流媒体服务器增删改查")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/monitor/media-server")
public class MediaServerController {

    private final MediaServerService mediaServerService;

    @OperationLog(module = "流媒体服务器", name = "分页查询流媒体服务器", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "分页查询流媒体服务器")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('monitor:media-server:list')")
    public Result<PageResult<MediaServerVO>> page(@ParameterObject @Validated MediaServerQueryDTO queryDTO) {
        return Result.success(mediaServerService.pageMediaServers(queryDTO));
    }

    @OperationLog(module = "流媒体服务器", name = "获取流媒体服务器详情", type = OperationType.QUERY, recordResult = false)
    @Operation(summary = "获取流媒体服务器详情")
    @Parameter(name = "id", description = "流媒体服务器ID", required = true, example = "1")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('monitor:media-server:detail')")
    public Result<MediaServerVO> getById(@PathVariable @NotNull(message = "流媒体服务器ID不能为空") Long id) {
        return Result.success(mediaServerService.getMediaServerById(id));
    }

    @OperationLog(module = "流媒体服务器", name = "新增流媒体服务器", type = OperationType.CREATE)
    @Operation(summary = "新增流媒体服务器")
    @PostMapping
    @PreAuthorize("hasAuthority('monitor:media-server:create')")
    public Result<Long> create(@RequestBody @Valid MediaServerCreateDTO createDTO) {
        return Result.success(mediaServerService.createMediaServer(createDTO));
    }

    @OperationLog(module = "流媒体服务器", name = "修改流媒体服务器", type = OperationType.UPDATE, recordResult = false)
    @Operation(summary = "修改流媒体服务器")
    @PutMapping
    @PreAuthorize("hasAuthority('monitor:media-server:update')")
    public Result<Void> update(@RequestBody @Valid MediaServerUpdateDTO updateDTO) {
        mediaServerService.updateMediaServer(updateDTO);
        return Result.success();
    }

    @OperationLog(module = "流媒体服务器", name = "删除流媒体服务器", type = OperationType.DELETE, recordResult = false)
    @Operation(summary = "逻辑删除流媒体服务器")
    @Parameter(name = "id", description = "流媒体服务器ID", required = true, example = "1")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('monitor:media-server:delete')")
    public Result<Void> delete(@PathVariable @NotNull(message = "流媒体服务器ID不能为空") Long id) {
        mediaServerService.deleteMediaServer(id);
        return Result.success();
    }
}