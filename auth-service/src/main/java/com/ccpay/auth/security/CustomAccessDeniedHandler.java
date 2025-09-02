package com.ccpay.auth.security;

import com.ccpay.common.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("ACCESS_DENIED")
                .message("You don't have permission to access this resource")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}