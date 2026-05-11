package com.km.taskflow.module.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户返回对象
 *
 * @author zzy
 */
@Schema(description = "用户返回对象")
@Data
public class UserVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "邮箱", example = "zzy@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    
    @Schema(description = "状态：0禁用，1启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}