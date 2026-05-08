package com.km.taskflow.module.system.dto;

import com.km.taskflow.common.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询参数
 *
 * @author zzy
 */
@Schema(description = "用户分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageQuery {

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;
    
    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;
}