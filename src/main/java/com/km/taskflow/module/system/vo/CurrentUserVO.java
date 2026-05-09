package com.km.taskflow.module.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 当前登录用户信息
 *
 * @author zzy
 */
@Schema(description = "当前登录用户信息")
@Data
public class CurrentUserVO {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "权限编码列表")
    private List<String> permissions;
}