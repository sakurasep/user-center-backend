package com.sakurasep.usercenter.exception;

import com.sakurasep.usercenter.common.ErrorCode;

/**
 * 自定义异常类
 * @author sakurasep
 * * @date 10/7/2025
 **/
public class BusinessException extends RuntimeException{
    private int code;
    private String desc;

    // 完整的自定义异常构造器，包含异常信息与详细异常描述
    public BusinessException(int code, String message, String desc) {
        super(message);
        this.code = code;
        this.desc = desc;
    }

    // 基础自定义异常构造器
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.desc = errorCode.getDesc();
    }

    // 自定义异常构造器，带自定义异常信息
    public BusinessException(ErrorCode errorCode, String desc) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
