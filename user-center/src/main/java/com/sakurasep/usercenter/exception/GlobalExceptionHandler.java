package com.sakurasep.usercenter.exception;

import com.sakurasep.usercenter.common.BaseResponse;
import com.sakurasep.usercenter.common.ErrorCode;
import com.sakurasep.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author sakurasep
 * * @date 11/7/2025
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExcetionHandler(BusinessException e) {
//        log.error("businessException: " + e.getMessage(), e);
        return ResultUtils.failure(e.getCode(), e.getMessage(), e.getDesc());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
//        log.error("runtimeException", e);
        return ResultUtils.failure(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }
}
