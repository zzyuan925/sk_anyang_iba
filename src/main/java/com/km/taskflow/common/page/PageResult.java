package com.km.taskflow.common.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author zzy
 * @description
 * @create 2026-05-07 15:55:35
 */
@Schema(description = "通用分页响应包装类")
@Data
public class PageResult<T> {
    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Long pageNum;

    /**
     * 每页条数
     */
    @Schema(description = "每页条数", example = "10")
    private Long pageSize;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "100")
    private Long total;

    /**
     * 总页数
     */
    @Schema(description = "总页数", example = "10")
    private Long pages;

    /**
     * 当前页数据
     */
    @Schema(description = "当前页数据")
    private List<T> records;

    private PageResult() {
    }
    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setPageNum(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setTotal(page.getTotal());
        result.setPages(page.getPages());
        result.setRecords(page.getRecords());
        return result;
    }

    public static <T> PageResult<T> of(List<T> records, Long total, Long pageNum, Long pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(total);
        result.setPages(pageSize == 0 ? 0 : (total + pageSize - 1) / pageSize);
        result.setRecords(records);
        return result;
    }
}
