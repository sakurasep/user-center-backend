package com.sakurasep.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * @author sakurasep
 * * @date 10/7/2025
 **/
@Data
public class BaseResponse<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    private String desc;

    // 默认构造器 状态码 + 数据 + 状态消息 + 状态描述
    public BaseResponse(int code, T data, String message, String desc) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.desc = desc;
    }

    // 重写构造器1:  状态码 +  数据 + 状态消息
    public BaseResponse(int code, T data, String message) {
        this(code, data, message, "");
    }

    // 重写构造器2:  状态码 +  数据
    public BaseResponse(int code, T data) {
        this(code, data, "", "");
    }

    // 错误状态构造器: 错误对象（状态码 + 数据 + 状态消息 + 状态描述）
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(),  errorCode.getDesc());
    }
}
