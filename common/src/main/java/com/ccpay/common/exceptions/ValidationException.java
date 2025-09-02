package com.ccpay.common.exceptions;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ValidationException extends BaseException {
    private final Map<String, String> fieldErrors;
    
    public ValidationException(String errorCode, String message) {
        super(errorCode, message);
        this.fieldErrors = new HashMap<>();
    }
    
    public ValidationException(String errorCode, String message, Map<String, String> fieldErrors) {
        super(errorCode, message);
        this.fieldErrors = fieldErrors;
    }
    
    public ValidationException addFieldError(String field, String message) {
        this.fieldErrors.put(field, message);
        return this;
    }
}