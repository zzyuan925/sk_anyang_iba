package com.sk.iba.common.result;

import com.sk.iba.common.enums.ResultCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zzy
 * @description
 * @create 2026-05-07 10:53:45
 */
@Schema(description = "通用响应包装类")
@Data
public class Result<T> {

    @Schema(description = "业务状态码", example = "200")
    private Integer code;

    @Schema(description = "提示消息", example = "操作成功")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    private Result() {
    }
    
    public static <T> Result<T> success(){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        return result;
    }
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.FAIL.getCode());
        result.setMessage(message);
        return result;
    }
    
    // 全局异常使用
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    public static <T> Result<T> fail(ResultCode resultCode) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        return result;
    }
    
    public static <T> Result<T> fail(ResultCode resultCode, String customMessage) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        // code 拿枚举里的，message 拿自己传的
        result.setMessage(customMessage);
        return result;
    }
}
