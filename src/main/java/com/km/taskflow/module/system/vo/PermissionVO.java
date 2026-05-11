package com.km.taskflow.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限返回对象
 *
 * @author zzy
 */
@Schema(description = "权限返回对象")
@Data
public class PermissionVO {

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

    @Schema(description = "前端路由或接口路径", example = "GET:/system/user/page")
    private String path;

    @Schema(description = "权限描述", example = "用户分页查询")
    private String description;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}