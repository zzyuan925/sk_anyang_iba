package com.sk.iba.module.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ROI类型返回对象
 *
 * @author zzy
 */
@Schema(description = "ROI类型返回对象")
@Data
public class RoiTypeVO {

    @Schema(description = "ROI类型ID", example = "1")
    private Long id;

    @Schema(description = "ROI类型名称", example = "检测区域")
    private String typeName;

    @Schema(description = "ROI类型编码", example = "detect_area")
    private String typeCode;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}