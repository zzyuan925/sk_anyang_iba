package com.km.taskflow.common.page;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * @author zzy
 * @description
 * @create 2026-05-07 15:54:44
 */
@Data
public class PageQuery {
    @Min(value = 1, message = "页码不能小于 1")
    private Long pageNum = 1L;

    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    private Long pageSize = 10L;
}
