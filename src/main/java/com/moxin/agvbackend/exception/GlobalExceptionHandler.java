package com.moxin.agvbackend.exception;


import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.utils.ResultCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public Result handleException(AppException e) {
        return Result.builder().code(e.getCode()).message(e.getMessage()).build();
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        return Result.builder().code(ResultCode.FAIL).message(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "操作失败").build();
    }
}
