package com.km.taskflow.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色返回对象
 *
 * @author zzy
 */
@Schema(description = "角色返回对象")
@Data
public class RoleVO {

    @Schema(description = "角色ID", example = "1")
    private Long id;

    @Schema(description = "角色名称", example = "项目经理")
    private String roleName;

    @Schema(description = "角色编码", example = "project_manager")
    private String roleCode;

    @Schema(description = "角色描述", example = "负责项目和任务分配")
    private String description;

    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}