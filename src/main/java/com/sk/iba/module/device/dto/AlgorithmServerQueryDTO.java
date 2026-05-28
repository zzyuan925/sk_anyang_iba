package com.sk.iba.module.device.dto;

import com.sk.iba.common.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 算法服务器分页查询参数
 *
 * @author zzy
 */
@Schema(description = "算法服务器分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class AlgorithmServerQueryDTO extends PageQuery {

    @Schema(description = "服务器名", example = "算法服务器")
    private String serverName;

    @Schema(description = "服务器IP", example = "192.168.1.100")
    private String ip;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;
}