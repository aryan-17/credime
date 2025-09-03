package com.ccpay.auth.security;

import com.ccpay.auth.dto.AuthResponse;
import com.ccpay.auth.service.OAuth2AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final OAuth2AuthService oAuth2AuthService;
    private final ObjectMapper objectMapper;
    
    @Value("${app.oauth2.success-redirect-url:http://localhost:3000/auth/callback}")
    private String redirectUrl;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
            
            try {
                AuthResponse authResponse = oAuth2AuthService.processOAuth2Login(authToken, request);
                
                // Redirect to frontend with tokens as query parameters
                String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                        .queryParam("access_token", authResponse.getAccessToken())
                        .queryParam("refresh_token", authResponse.getRefreshToken())
                        .queryParam("token_type", authResponse.getTokenType())
                        .queryParam("expires_in", authResponse.getExpiresIn())
                        .build()
                        .toUriString();
                
                getRedirectStrategy().sendRedirect(request, response, targetUrl);
                
            } catch (Exception e) {
                log.error("OAuth2 authentication processing failed", e);
                response.sendRedirect(redirectUrl + "?error=authentication_failed");
            }
        }
    }
}