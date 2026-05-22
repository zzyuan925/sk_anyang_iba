package com.sk.taskflow.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改权限参数
 *
 * @author zzy
 */
@Schema(description = "修改权限参数")
@Data
public class PermissionUpdateDTO {

    @Schema(description = "权限ID", example = "1")
    @NotNull(message = "权限ID不能为空")
    private Long id;

    @Schema(description = "权限名称", example = "用户查询")
    @Size(max = 50, message = "权限名称长度不能超过 50")
    private String permissionName;

    @Schema(description = "权限类型：1菜单，2按钮，3接口", example = "3")
    private Integer permissionType;

    @Schema(description = "父级权限ID", example = "0")
    private Long parentId;

    @Schema(description = "前端路由或接口路径", example = "GET:/system/user/page")
    @Size(max = 200, message = "路径长度不能超过 200")
    private String path;

    @Schema(description = "权限描述", example = "用户分页查询")
    @Size(max = 255, message = "权限描述长度不能超过 255")
    private String description;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;
}