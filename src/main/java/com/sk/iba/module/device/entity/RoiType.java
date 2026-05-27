package com.sk.iba.module.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sk.iba.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ROI类型实体
 *
 * @author zzy
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("biz_roi_type")
public class RoiType extends BaseEntity {

    private String typeName;

    private String typeCode;

    /**
     * 状态：0禁用，1启用
     */
    private Integer status;

    private String remark;
}