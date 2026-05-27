package com.sk.iba.module.monitor.dto;

import com.sk.iba.common.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流媒体服务器分页查询参数
 *
 * @author zzy
 */
@Schema(description = "流媒体服务器分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class MediaServerQueryDTO extends PageQuery {

    @Schema(description = "流媒体服务器名称", example = "本地ZLM")
    private String serverName;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;
}