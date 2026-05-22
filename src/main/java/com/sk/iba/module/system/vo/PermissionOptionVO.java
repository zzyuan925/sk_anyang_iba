package com.sk.iba.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 权限下拉选项
 *
 * @author zzy
 */
@Schema(description = "权限下拉选项")
@Data
public class PermissionOptionVO {

    @Schema(description = "权限ID", example = "1")
    private Long id;

    @Schema(description = "权限名称", example = "用户查询")
    private String permissionName;

    @Schema(description = "权限编码", example = "system:user:list")
    private String permissionCode;

    @Schema(description = "权限类型：1菜单，2按钮，3接口", example = "3")
    private Integer permissionType;

    @Schema(description = "父级权限ID", example = "0")
    private Long parentId;
}