package com.sakurasep.usercenter.common;

/**
 * 返回工具类
 *
 * @author sakurasep
 * * @date 10/7/2025
 **/
public class ResultUtils {

    /**
     * 成功
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, data, "ok");
    }

    /**
     * 失败
     *
     */
    public static BaseResponse failure(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     */
    public static BaseResponse failure(int code, String message, String desc) {
        return new BaseResponse(code, null, message, desc);
    }

    /**
     */
    public static BaseResponse failure(ErrorCode errorCode, String message, String desc) {
        return new BaseResponse(errorCode.getCode(), null, message, desc);
    }

    /**
     */
    public static BaseResponse failure(ErrorCode errorCode, String desc) {
        return new BaseResponse(errorCode.getCode(), errorCode.getMessage(), desc);
    }


}
