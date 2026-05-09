package com.km.taskflow.module.system.vo;

import com.km.taskflow.common.constant.AuthConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录返回对象
 *
 * @author zzy
 */
@Schema(description = "登录返回对象")
@Data
public class LoginVO {

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = AuthConstants.TOKEN_TYPE_BEARER;
}