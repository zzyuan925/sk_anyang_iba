package com.sk.iba.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户分配区域参数
 *
 * @author zzy
 */
@Schema(description = "用户分配区域参数")
@Data
public class UserAssignRegionDTO {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "区域ID列表")
    private List<Long> regionIds;
}