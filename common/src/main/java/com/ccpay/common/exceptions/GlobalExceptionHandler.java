package com.ccpay.common.exceptions;

import com.ccpay.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("ResourceNotFoundException [{}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("ValidationException [{}]: {}", traceId, ex.getMessage());
        
        List<ErrorResponse.FieldError> fieldErrors = ex.getFieldErrors().entrySet().stream()
                .map(entry -> ErrorResponse.FieldError.builder()
                        .field(entry.getKey())
                        .message(entry.getValue())
                        .build())
                .collect(Collectors.toList());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .fieldErrors(fieldErrors)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("MethodArgumentNotValidException [{}]: {}", traceId, ex.getMessage());
        
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("VALIDATION_FAILED")
                .message("Validation failed for one or more fields")
                .fieldErrors(fieldErrors)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("BusinessException [{}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("UnauthorizedException [{}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("AccessDeniedException [{}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("ACCESS_DENIED")
                .message("You don't have permission to access this resource")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("BadCredentialsException [{}]: {}", traceId, ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("INVALID_CREDENTIALS")
                .message("Invalid username or password")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("DataIntegrityViolationException [{}]: {}", traceId, ex.getMessage());
        
        String message = "Database constraint violation";
        if (ex.getMessage() != null && ex.getMessage().contains("duplicate key")) {
            message = "Record already exists";
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("DATA_INTEGRITY_VIOLATION")
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        String traceId = generateTraceId();
        log.error("Unhandled exception [{}]: ", traceId, ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    private ErrorResponse.FieldError mapFieldError(FieldError fieldError) {
        return ErrorResponse.FieldError.builder()
                .field(fieldError.getField())
                .rejectedValue(fieldError.getRejectedValue())
                .message(fieldError.getDefaultMessage())
                .code(fieldError.getCode())
                .build();
    }
    
    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}