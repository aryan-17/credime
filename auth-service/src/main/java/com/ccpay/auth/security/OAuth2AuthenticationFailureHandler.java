package com.ccpay.auth.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    
    @Value("${app.oauth2.failure-redirect-url:http://localhost:3000/auth/error}")
    private String redirectUrl;
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        String errorMessage = exception.getMessage();
        log.error("OAuth2 authentication failed: {}", errorMessage);
        
        // Log security event
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        log.warn("Security Event - Type: OAUTH2_AUTH_FAILED, IP: {}, UserAgent: {}, Error: {}", 
                ipAddress, userAgent, errorMessage);
        
        // Redirect to frontend with error message
        String encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        getRedirectStrategy().sendRedirect(request, response, 
                redirectUrl + "?error=" + encodedError);
    }
}