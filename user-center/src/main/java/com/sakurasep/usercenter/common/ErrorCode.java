package com.sakurasep.usercenter.common;

/**
 * 错误码
 * @author sakurasep
 * * @date 10/7/2025
 **/
public enum ErrorCode {
    SUCCESS(20000, "成功", "操作成功"),
    PARAMS_ERROR(40000, "请求参数错误", "参数错误"),
    NULL_ERROR(40001, "请求参数为空", "参数为空"),
    NO_AUTH_ERROR(40100, "无权限", "权限受限"),
    NOT_LOGIN(40101, "没有登录", "没有登录"),
    NOT_REPEAT(40002, "不能重复", "重复"),
    OP_ERROR(40400,"操作失败", "操作失败"),
    SYSTEM_ERROR(50000, "系统内部异常", "系统错误"),
    LOGIN_ERROR(40400, "登录异常", "登录失败");
    private final int code;
    private final String message;
    private final String desc;

    ErrorCode(int code, String message, String desc) {
        this.code = code;
        this.message = message;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDesc() {
        return desc;
    }
}




