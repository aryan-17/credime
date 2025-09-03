package com.ccpay.auth.controller;

import com.ccpay.auth.dto.AuthResponse;
import com.ccpay.auth.dto.RefreshTokenRequest;
import com.ccpay.auth.service.OAuth2AuthService;
import com.ccpay.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "OAuth2 Authentication", description = "OAuth2-based authentication endpoints")
public class OAuth2Controller {
    
    private final OAuth2AuthService oAuth2AuthService;
    
    @GetMapping("/oauth2/success")
    @Operation(summary = "Handle OAuth2 login success")
    public ResponseEntity<ApiResponse<AuthResponse>> oauth2Success(
            OAuth2AuthenticationToken authentication,
            HttpServletRequest request) {
        AuthResponse response = oAuth2AuthService.processOAuth2Login(authentication, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = oAuth2AuthService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Logout and invalidate refresh token")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequest request) {
        oAuth2AuthService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}