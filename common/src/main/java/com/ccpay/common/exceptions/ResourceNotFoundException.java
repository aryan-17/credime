package com.ccpay.common.exceptions;

public class ResourceNotFoundException extends BaseException {
    
    public ResourceNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
    
    public ResourceNotFoundException(String resourceName, Object id) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s not found with id: '%s'", resourceName, id));
    }
}