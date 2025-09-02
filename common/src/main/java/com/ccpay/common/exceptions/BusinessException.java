package com.ccpay.common.exceptions;

public class BusinessException extends BaseException {
    
    public BusinessException(String errorCode, String message) {
        super(errorCode, message);
    }
    
    public BusinessException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
    
    public BusinessException(String errorCode, String message, Object... args) {
        super(errorCode, message, args);
    }
}