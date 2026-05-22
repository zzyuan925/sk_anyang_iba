package com.sk.taskflow.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色下拉选项
 *
 * @author zzy
 */
@Schema(description = "角色下拉选项")
@Data
public class RoleOptionVO {

    @Schema(description = "角色ID", example = "1")
    private Long id;

    @Schema(description = "角色名称", example = "超级管理员")
    private String roleName;

    @Schema(description = "角色编码", example = "admin")
    private String roleCode;
}