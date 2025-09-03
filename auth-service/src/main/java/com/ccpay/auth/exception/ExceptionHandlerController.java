package com.ccpay.auth.exception;

import com.ccpay.common.constants.ErrorCodes;
import com.ccpay.common.dto.ErrorResponse;
import com.ccpay.common.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        String path = getPath(request);
        String traceId = generateTraceId();
        
        log.error("Unauthorized access attempt - TraceId: {}, Path: {}, Message: {}", 
                traceId, path, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(path)
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        String path = getPath(request);
        String traceId = generateTraceId();
        
        logSecurityEvent(request, "INVALID_CREDENTIALS", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodes.INVALID_CREDENTIALS)
                .message("Invalid email or password")
                .path(path)
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLockedException(
            LockedException ex, WebRequest request) {
        String path = getPath(request);
        String traceId = generateTraceId();
        
        logSecurityEvent(request, "ACCOUNT_LOCKED", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodes.ACCOUNT_LOCKED)
                .message("Account is locked due to multiple failed login attempts")
                .path(path)
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledException(
            DisabledException ex, WebRequest request) {
        String path = getPath(request);
        String traceId = generateTraceId();
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodes.ACCOUNT_NOT_VERIFIED)
                .message("Account is not verified. Please verify your email first")
                .path(path)
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        String path = getPath(request);
        String traceId = generateTraceId();
        
        logSecurityEvent(request, "AUTHENTICATION_FAILED", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodes.INVALID_CREDENTIALS)
                .message("Authentication failed")
                .path(path)
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        String path = getPath(request);
        String traceId = generateTraceId();
        
        log.warn("Access denied - TraceId: {}, Path: {}, Message: {}", 
                traceId, path, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodes.INSUFFICIENT_PERMISSIONS)
                .message("You don't have permission to access this resource")
                .path(path)
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, WebRequest request) {
        String path = getPath(request);
        String traceId = generateTraceId();
        
        log.warn("Validation error - TraceId: {}, Path: {}, Message: {}", 
                traceId, path, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(path)
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, WebRequest request) {
        String path = getPath(request);
        String traceId = generateTraceId();
        
        log.error("Business error - TraceId: {}, Path: {}, ErrorCode: {}, Message: {}", 
                traceId, path, ex.getErrorCode(), ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(path)
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        String path = getPath(request);
        String traceId = generateTraceId();
        
        log.warn("Resource not found - TraceId: {}, Path: {}, Message: {}", 
                traceId, path, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(path)
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        String path = getPath(request);
        String traceId = generateTraceId();
        
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    Object rejectedValue = ((FieldError) error).getRejectedValue();
                    String message = error.getDefaultMessage();
                    String code = error.getCode();
                    
                    return ErrorResponse.FieldError.builder()
                            .field(fieldName)
                            .rejectedValue(rejectedValue)
                            .message(message)
                            .code(code)
                            .build();
                })
                .collect(Collectors.toList());
        
        log.warn("Validation failed - TraceId: {}, Path: {}, Errors: {}", 
                traceId, path, fieldErrors.size());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodes.VALIDATION_FAILED)
                .message("Validation failed")
                .path(path)
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .fieldErrors(fieldErrors)
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        String path = getPath(request);
        String traceId = generateTraceId();
        
        log.error("Unexpected error - TraceId: {}, Path: {}, Error: ", 
                traceId, path, ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodes.INTERNAL_SERVER_ERROR)
                .message("An unexpected error occurred. Please try again later")
                .path(path)
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private String getPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            HttpServletRequest httpRequest = ((ServletWebRequest) request).getRequest();
            return httpRequest.getRequestURI();
        }
        return null;
    }
    
    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }
    
    private void logSecurityEvent(WebRequest request, String eventType, String description) {
        if (request instanceof ServletWebRequest) {
            HttpServletRequest httpRequest = ((ServletWebRequest) request).getRequest();
            String ipAddress = httpRequest.getRemoteAddr();
            String userAgent = httpRequest.getHeader("User-Agent");
            
            log.warn("Security Event - Type: {}, IP: {}, UserAgent: {}, Description: {}", 
                    eventType, ipAddress, userAgent, description);
            
            // TODO: Store in security_events table as per design document
        }
    }
}
