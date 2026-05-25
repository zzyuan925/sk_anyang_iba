package com.sk.iba.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限树返回对象
 * 
 * @author zzy
 */
@Data
public class PermissionTreeVO {

    @Schema(description = "权限ID", example = "1")
    private Long id;

    @Schema(description = "权限名称", example = "系统管理")
    private String permissionName;

    @Schema(description = "权限编码", example = "system")
    private String permissionCode;

    @Schema(description = "权限类型：1菜单，2操作，3接口", example = "1")
    private Integer permissionType;

    @Schema(description = "父级权限ID", example = "0")
    private Long parentId;

    @Schema(description = "前端路由或接口路径", example = "/system/user")
    private String path;

    @Schema(description = "权限描述", example = "系统管理菜单")
    private String description;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "子权限")
    private List<PermissionTreeVO> children = new ArrayList<>();
}