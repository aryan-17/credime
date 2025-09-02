package com.ccpay.auth.controller;

import com.ccpay.auth.dto.*;
import com.ccpay.auth.service.AuthService;
import com.ccpay.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        AuthResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Logout and invalidate refresh token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @GetMapping("/mfa/setup")
    @Operation(summary = "Get MFA setup information")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MfaSetupResponse>> setupMfa(Principal principal) {
        MfaSetupResponse response = authService.setupMfa(principal.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/mfa/enable")
    @Operation(summary = "Enable MFA with verification code")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> enableMfa(
            @Valid @RequestBody MfaEnableRequest request,
            Principal principal) {
        authService.enableMfa(principal.getName(), request.getSecret(), request.getCode());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @PostMapping("/mfa/disable")
    @Operation(summary = "Disable MFA")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> disableMfa(
            @Valid @RequestBody MfaDisableRequest request,
            Principal principal) {
        authService.disableMfa(principal.getName(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @PostMapping("/verify-email")
    @Operation(summary = "Verify email address")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        authService.verifyEmail(request.getEmail(), request.getToken());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @PostMapping("/resend-verification")
    @Operation(summary = "Resend email verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        authService.resendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @PostMapping("/change-password")
    @Operation(summary = "Change password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Principal principal) {
        authService.changePassword(principal.getName(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}