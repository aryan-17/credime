package com.ccpay.auth.service;

import com.ccpay.auth.dto.*;
import com.ccpay.auth.entity.RefreshToken;
import com.ccpay.auth.entity.Role;
import com.ccpay.auth.entity.User;
import com.ccpay.auth.entity.enums.UserStatus;
import com.ccpay.auth.repository.RefreshTokenRepository;
import com.ccpay.auth.repository.RoleRepository;
import com.ccpay.auth.repository.UserRepository;
import com.ccpay.auth.security.CustomUserDetails;
import com.ccpay.auth.security.JwtService;
import com.ccpay.common.constants.ApplicationConstants;
import com.ccpay.common.constants.ErrorCodes;
import com.ccpay.common.constants.SecurityConstants;
import com.ccpay.common.exceptions.BusinessException;
import com.ccpay.common.exceptions.UnauthorizedException;
import com.ccpay.common.utils.SecurityUtils;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final GoogleAuthenticator googleAuthenticator;
    private final EmailService emailService;
    
    @Value("${app.mfa.issuer:CC AutoPay}")
    private String mfaIssuer;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, 
                    "User with this email already exists");
        }
        
        // Get default user role
        Role userRole = roleRepository.findByName(SecurityConstants.ROLE_USER)
                .orElseThrow(() -> new BusinessException(ErrorCodes.INTERNAL_SERVER_ERROR, 
                        "Default user role not found"));
        
        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .status(UserStatus.ACTIVE)
                .roles(Set.of(userRole))
                .build();
        
        user = userRepository.save(user);
        
        // Send verification email
        emailService.sendVerificationEmail(user);
        
        // Generate tokens
        CustomUserDetails userDetails = CustomUserDetails.create(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = createRefreshToken(user, null);
        
        return buildAuthResponse(user, accessToken, refreshToken);
    }
    
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException(ErrorCodes.INVALID_CREDENTIALS, 
                        "Invalid credentials"));
        
        // Check if account is locked
        if (user.isAccountLocked()) {
            throw new UnauthorizedException(ErrorCodes.ACCOUNT_LOCKED, 
                    "Account is locked due to multiple failed login attempts");
        }
        
        // Check if email is verified
        if (!user.isEmailVerified()) {
            throw new UnauthorizedException(ErrorCodes.ACCOUNT_NOT_VERIFIED, 
                    "Please verify your email before logging in");
        }
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            // Reset failed login attempts
            user.resetFailedLoginAttempts();
            
            // Check if MFA is enabled
            if (user.isMfaEnabled()) {
                // If MFA code is not provided, return MFA required response
                if (request.getMfaCode() == null || request.getMfaCode().isEmpty()) {
                    String mfaToken = jwtService.generateToken(
                            java.util.Map.of(SecurityConstants.CLAIM_TOKEN_TYPE, "MFA_REQUIRED"),
                            CustomUserDetails.create(user),
                            300000 // 5 minutes
                    );
                    
                    return AuthResponse.builder()
                            .mfaRequired(true)
                            .mfaToken(mfaToken)
                            .build();
                }
                
                // Verify MFA code
                if (!googleAuthenticator.authorize(user.getMfaSecret(), Integer.parseInt(request.getMfaCode()))) {
                    throw new UnauthorizedException(ErrorCodes.INVALID_MFA_CODE, "Invalid MFA code");
                }
            }
            
            // Update last login
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Generate tokens
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = createRefreshToken(user, httpRequest);
            
            return buildAuthResponse(user, accessToken, refreshToken);
            
        } catch (BadCredentialsException e) {
            // Increment failed login attempts
            user.incrementFailedLoginAttempts();
            
            // Lock account if max attempts exceeded
            if (user.getFailedLoginAttempts() >= ApplicationConstants.MAX_FAILED_LOGIN_ATTEMPTS) {
                user.lockAccount(ApplicationConstants.ACCOUNT_LOCK_DURATION_MINUTES);
            }
            
            userRepository.save(user);
            
            throw new UnauthorizedException(ErrorCodes.INVALID_CREDENTIALS, "Invalid credentials");
        }
    }
    
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException(ErrorCodes.TOKEN_EXPIRED, 
                        "Invalid refresh token"));
        
        // Validate token
        if (!refreshToken.isValid()) {
            throw new UnauthorizedException(ErrorCodes.TOKEN_EXPIRED, "Refresh token expired or revoked");
        }
        
        User user = refreshToken.getUser();
        CustomUserDetails userDetails = CustomUserDetails.create(user);
        
        // Generate new access token
        String accessToken = jwtService.generateAccessToken(userDetails);
        
        // Optionally rotate refresh token
        String newRefreshToken = createRefreshToken(user, null);
        
        // Revoke old refresh token
        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshToken.setRevokedReason("Token rotation");
        refreshTokenRepository.save(refreshToken);
        
        return buildAuthResponse(user, accessToken, newRefreshToken);
    }
    
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            token.setRevokedAt(LocalDateTime.now());
            token.setRevokedReason("User logout");
            refreshTokenRepository.save(token);
        });
    }
    
    @Transactional
    public MfaSetupResponse setupMfa(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException(ErrorCodes.USER_NOT_FOUND, "User not found"));
        
        // Generate MFA secret
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        String secret = key.getKey();
        
        // Save secret temporarily (will be confirmed later)
        String qrCodeUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL(
                mfaIssuer,
                user.getEmail(),
                key
        );
        
        return MfaSetupResponse.builder()
                .secret(secret)
                .qrCode(qrCodeUrl)
                .build();
    }
    
    @Transactional
    public void enableMfa(String userId, String secret, String code) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException(ErrorCodes.USER_NOT_FOUND, "User not found"));
        
        // Verify the code
        if (!googleAuthenticator.authorize(secret, Integer.parseInt(code))) {
            throw new BusinessException(ErrorCodes.INVALID_MFA_CODE, "Invalid MFA code");
        }
        
        // Enable MFA
        user.setMfaEnabled(true);
        user.setMfaSecret(secret);
        userRepository.save(user);
    }
    
    private String createRefreshToken(User user, HttpServletRequest request) {
        String token = SecurityUtils.generateSecureToken();
        
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .deviceInfo(request != null ? request.getHeader("User-Agent") : null)
                .ipAddress(request != null ? getClientIp(request) : null)
                .build();
        
        refreshTokenRepository.save(refreshToken);
        
        return token;
    }
    
    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(AuthResponse.UserDto.builder()
                        .userId(user.getUserId().toString())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .emailVerified(user.isEmailVerified())
                        .mfaEnabled(user.isMfaEnabled())
                        .kycStatus(user.getKycStatus().toString())
                        .build())
                .build();
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
    
    @Transactional
    public void disableMfa(String userId, String password) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException(ErrorCodes.USER_NOT_FOUND, "User not found"));
        
        // Verify password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException(ErrorCodes.INVALID_CREDENTIALS, "Invalid password");
        }
        
        // Disable MFA
        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);
    }
    
    @Transactional
    public void verifyEmail(String email, String token) {
        // Implementation would check email verification token
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCodes.USER_NOT_FOUND, "User not found"));
        
        // For now, just mark as verified
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCodes.USER_NOT_FOUND, "User not found"));
        
        if (user.isEmailVerified()) {
            throw new BusinessException(ErrorCodes.INVALID_REQUEST_FORMAT, "Email already verified");
        }
        
        emailService.sendVerificationEmail(user);
    }
    
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCodes.USER_NOT_FOUND, "User not found"));
        
        String resetToken = SecurityUtils.generateSecureToken();
        // Store reset token in database (implementation needed)
        
        emailService.sendPasswordResetEmail(user, resetToken);
    }
    
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Implementation would verify reset token
        // For now, this is a placeholder
        throw new BusinessException(ErrorCodes.INTERNAL_SERVER_ERROR, "Password reset not yet implemented");
    }
    
    @Transactional
    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException(ErrorCodes.USER_NOT_FOUND, "User not found"));
        
        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new UnauthorizedException(ErrorCodes.INVALID_CREDENTIALS, "Invalid old password");
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Revoke all refresh tokens
        refreshTokenRepository.revokeAllUserTokens(user, LocalDateTime.now(), "Password changed");
    }
}