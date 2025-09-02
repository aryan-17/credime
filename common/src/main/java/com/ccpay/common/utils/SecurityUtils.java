package com.ccpay.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class SecurityUtils {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    
    private SecurityUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            return Optional.of((String) principal);
        }
        
        return Optional.empty();
    }
    
    public static Optional<UUID> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        // Assuming the principal contains user ID information
        // This needs to be implemented based on your UserDetails implementation
        return Optional.empty();
    }
    
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
    
    public static String generateSecureToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
    
    public static String generateSecureToken(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
    
    public static String generateNumericOTP(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }
    
    public static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}