package com.km.taskflow.common.exception;

import com.km.taskflow.common.result.ResultCode;
import lombok.Getter;

/**
 * @author zzy
 * @description
 * @create 2026-05-07 11:14:13
 */
@Getter
public class BusinessException extends RuntimeException{
    
    private final Integer code;

    /**
     * 场景 1：只传错误信息，状态码默认使用全局的 FAIL
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
    }

    /**
     * 场景 2：直接抛出具体的枚举错误
     * 例如：throw new BusinessException(ResultCode.NOT_FOUND);
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * 场景 3：抛出枚举错误，但覆盖错误信息
     * 例如：throw new BusinessException(ResultCode.PARAM_ERROR, "邮箱格式不正确");
     */
    public BusinessException(ResultCode resultCode, String customMessage) {
        super(customMessage);
        this.code = resultCode.getCode();
    }
}
