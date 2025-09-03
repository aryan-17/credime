package com.ccpay.auth.service;

import com.ccpay.auth.dto.AuthResponse;
import com.ccpay.auth.entity.OAuth2Provider;
import com.ccpay.auth.entity.RefreshToken;
import com.ccpay.auth.entity.Role;
import com.ccpay.auth.entity.User;
import com.ccpay.auth.entity.enums.UserStatus;
import com.ccpay.auth.repository.RefreshTokenRepository;
import com.ccpay.auth.repository.RoleRepository;
import com.ccpay.auth.repository.UserRepository;
import com.ccpay.auth.security.CustomUserDetails;
import com.ccpay.auth.security.JwtService;
import com.ccpay.common.constants.ErrorCodes;
import com.ccpay.common.constants.SecurityConstants;
import com.ccpay.common.exceptions.BusinessException;
import com.ccpay.common.exceptions.UnauthorizedException;
import com.ccpay.common.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    
    @Transactional
    public AuthResponse processOAuth2Login(OAuth2AuthenticationToken authentication, HttpServletRequest request) {
        OAuth2User oAuth2User = authentication.getPrincipal();
        String registrationId = authentication.getAuthorizedClientRegistrationId();
        
        OAuth2Provider provider = OAuth2Provider.valueOf(registrationId.toUpperCase());
        String email = extractEmail(oAuth2User, provider);
        String oauthId = extractOAuthId(oAuth2User, provider);
        
        if (email == null) {
            throw new BusinessException(ErrorCodes.INVALID_USER_DATA, 
                    "Email not provided by OAuth2 provider");
        }
        
        User user = findOrCreateUser(oAuth2User, provider, email, oauthId);
        
        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate tokens
        CustomUserDetails userDetails = CustomUserDetails.create(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = createRefreshToken(user, request);
        
        return buildAuthResponse(user, accessToken, refreshToken);
    }
    
    @Transactional
    public User findOrCreateUser(OAuth2User oAuth2User, OAuth2Provider provider, 
                                 String email, String oauthId) {
        // First try to find by OAuth provider and ID
        Optional<User> existingUser = userRepository.findByOauthProviderAndOauthId(provider, oauthId);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        
        // Check if user exists with same email but different provider
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS,
                    "An account with this email already exists. Please login with " + 
                    userByEmail.get().getOauthProvider().name().toLowerCase());
        }
        
        // Create new user
        return createNewOAuth2User(oAuth2User, provider, email, oauthId);
    }
    
    private User createNewOAuth2User(OAuth2User oAuth2User, OAuth2Provider provider,
                                     String email, String oauthId) {
        Role userRole = roleRepository.findByName(SecurityConstants.ROLE_USER)
                .orElseThrow(() -> new BusinessException(ErrorCodes.INTERNAL_SERVER_ERROR, 
                        "Default user role not found"));
        
        String name = extractName(oAuth2User, provider);
        String[] nameParts = name != null ? name.split(" ", 2) : new String[]{"", ""};
        
        User user = User.builder()
                .email(email)
                .oauthProvider(provider)
                .oauthId(oauthId)
                .firstName(nameParts[0])
                .lastName(nameParts.length > 1 ? nameParts[1] : "")
                .profileImageUrl(extractProfileImageUrl(oAuth2User, provider))
                .status(UserStatus.ACTIVE)
                .emailVerified(true) // OAuth2 providers usually verify email
                .emailVerifiedAt(LocalDateTime.now())
                .roles(Set.of(userRole))
                .build();
        
        return userRepository.save(user);
    }
    
    @Transactional
    public AuthResponse refreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new UnauthorizedException(ErrorCodes.TOKEN_EXPIRED, 
                        "Invalid refresh token"));
        
        if (!refreshToken.isValid()) {
            throw new UnauthorizedException(ErrorCodes.TOKEN_EXPIRED, 
                    "Refresh token expired or revoked");
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
    
    private String extractEmail(OAuth2User oAuth2User, OAuth2Provider provider) {
        return oAuth2User.getAttribute("email");
    }
    
    private String extractOAuthId(OAuth2User oAuth2User, OAuth2Provider provider) {
        return switch (provider) {
            case GOOGLE -> oAuth2User.getAttribute("sub");
            case FACEBOOK -> oAuth2User.getAttribute("id");
            case GITHUB -> String.valueOf(oAuth2User.getAttribute("id"));
        };
    }
    
    private String extractName(OAuth2User oAuth2User, OAuth2Provider provider) {
        return oAuth2User.getAttribute("name");
    }
    
    private String extractProfileImageUrl(OAuth2User oAuth2User, OAuth2Provider provider) {
        return switch (provider) {
            case GOOGLE -> oAuth2User.getAttribute("picture");
            case FACEBOOK -> {
                Map<String, Object> picture = oAuth2User.getAttribute("picture");
                if (picture != null && picture.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) picture.get("data");
                    yield (String) data.get("url");
                }
                yield null;
            }
            case GITHUB -> oAuth2User.getAttribute("avatar_url");
        };
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
                        .profileImageUrl(user.getProfileImageUrl())
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
}