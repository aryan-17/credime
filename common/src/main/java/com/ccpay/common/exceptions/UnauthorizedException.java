package com.ccpay.common.exceptions;

public class UnauthorizedException extends BaseException {
    
    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message);
    }
    
    public UnauthorizedException(String errorCode, String message) {
        super(errorCode, message);
    }
    
    public UnauthorizedException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}