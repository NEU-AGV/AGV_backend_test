package com.moxin.agvbackend.exception;

public class AppException extends RuntimeException{
    private final int code;
    private final String message;

    public AppException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
