package com.sk.iba.module.system.dto;

import com.sk.iba.common.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色分页查询参数
 *
 * @author zzy
 */
@Schema(description = "角色分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQueryDTO extends PageQuery {

    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    @Schema(description = "角色编码", example = "admin")
    private String roleCode;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;
}