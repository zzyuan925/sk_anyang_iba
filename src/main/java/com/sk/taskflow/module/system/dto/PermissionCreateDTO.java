package com.sk.taskflow.module.system.dto;

import com.sk.taskflow.common.constant.SystemConstants;
import com.sk.taskflow.common.enums.PermissionTypeEnum;
import com.sk.taskflow.common.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增权限参数
 *
 * @author zzy
 */
@Schema(description = "新增权限参数")
@Data
public class PermissionCreateDTO {

    @Schema(description = "权限名称", example = "用户查询")
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 50, message = "权限名称长度不能超过 50")
    private String permissionName;

    @Schema(description = "权限编码", example = "system:user:list")
    @NotBlank(message = "权限编码不能为空")
    @Size(max = 100, message = "权限编码长度不能超过 100")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9:._-]*$", message = "权限编码格式不正确")
    private String permissionCode;

    @Schema(description = "权限类型：1菜单，2按钮，3接口", example = "3")
    private Integer permissionType = PermissionTypeEnum.API.getCode();

    @Schema(description = "父级权限ID", example = "0")
    private Long parentId = SystemConstants.ROOT_PARENT_ID;

    @Schema(description = "前端路由或接口路径", example = "GET:/system/user/page")
    @Size(max = 200, message = "路径长度不能超过 200")
    private String path;

    @Schema(description = "权限描述", example = "用户分页查询")
    @Size(max = 255, message = "权限描述长度不能超过 255")
    private String description;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status = StatusEnum.ENABLED.getCode();
}