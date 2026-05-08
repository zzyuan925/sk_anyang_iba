package com.km.taskflow.module.system.dto;

import com.km.taskflow.common.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限分页查询参数
 *
 * @author zzy
 */
@Schema(description = "权限分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionQueryDTO extends PageQuery {

    @Schema(description = "权限名称", example = "用户查询")
    private String permissionName;

    @Schema(description = "权限编码", example = "system:user:list")
    private String permissionCode;

    @Schema(description = "权限类型：1菜单，2按钮，3接口", example = "3")
    private Integer permissionType;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;
}