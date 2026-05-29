package com.sk.iba.module.device.dto;

import com.sk.iba.common.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 算法运行分页查询参数
 *
 * @author zzy
 */
@Schema(description = "算法运行分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class AlgorithmRuntimeQueryDTO extends PageQuery {

    @Schema(description = "算法服务器ID")
    private Long serverId;

    @Schema(description = "算法功能ID")
    private Long functionId;

    @Schema(description = "算法包ID")
    private Long algorithmPackageId;

    @Schema(description = "运行状态：0未运行，1运行中，2运行异常")
    private Integer runStatus;
}