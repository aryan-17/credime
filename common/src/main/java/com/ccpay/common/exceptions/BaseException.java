package com.ccpay.common.exceptions;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final String errorCode;
    private final Object[] args;
    
    protected BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = null;
    }
    
    protected BaseException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = null;
    }
    
    protected BaseException(String errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }
    
    protected BaseException(String errorCode, String message, Throwable cause, Object... args) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = args;
    }
}