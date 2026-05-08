package com.km.taskflow.common.page;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * @author zzy
 * @description
 * @create 2026-05-07 15:54:44
 */
@Schema(description = "基础分页查询参数")
@Data
public class PageQuery {
    @Schema(description = "页码", example = "1", defaultValue = "1")
    @Min(value = 1, message = "页码不能小于 1")
    private Long pageNum = 1L;

    @Schema(description = "每页条数", example = "10", defaultValue = "10")
    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    private Long pageSize = 10L;
}
