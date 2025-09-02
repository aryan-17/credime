package com.ccpay.common.constants;

public class SecurityConstants {
    
    // JWT
    public static final String JWT_SECRET_KEY = "${jwt.secret}";
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_ISSUER = "cc-autopay-system";
    public static final String JWT_AUDIENCE = "cc-autopay-client";
    
    // JWT Claims
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_AUTHORITIES = "authorities";
    public static final String CLAIM_TOKEN_TYPE = "tokenType";
    
    // Token Types
    public static final String TOKEN_TYPE_ACCESS = "ACCESS";
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";
    public static final String TOKEN_TYPE_EMAIL_VERIFICATION = "EMAIL_VERIFICATION";
    public static final String TOKEN_TYPE_PASSWORD_RESET = "PASSWORD_RESET";
    
    // Roles
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_SUPPORT = "ROLE_SUPPORT";
    public static final String ROLE_SYSTEM = "ROLE_SYSTEM";
    
    // Authorities
    public static final String AUTHORITY_USER_READ = "user:read";
    public static final String AUTHORITY_USER_WRITE = "user:write";
    public static final String AUTHORITY_WALLET_READ = "wallet:read";
    public static final String AUTHORITY_WALLET_WRITE = "wallet:write";
    public static final String AUTHORITY_CARD_READ = "card:read";
    public static final String AUTHORITY_CARD_WRITE = "card:write";
    public static final String AUTHORITY_TRANSACTION_READ = "transaction:read";
    public static final String AUTHORITY_TRANSACTION_WRITE = "transaction:write";
    public static final String AUTHORITY_ADMIN_READ = "admin:read";
    public static final String AUTHORITY_ADMIN_WRITE = "admin:write";
    
    // Security Headers
    public static final String HEADER_CSRF_TOKEN = "X-CSRF-TOKEN";
    public static final String HEADER_API_KEY = "X-API-Key";
    public static final String HEADER_CLIENT_ID = "X-Client-Id";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    
    // Password Policy
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 128;
    public static final boolean PASSWORD_REQUIRE_UPPERCASE = true;
    public static final boolean PASSWORD_REQUIRE_LOWERCASE = true;
    public static final boolean PASSWORD_REQUIRE_DIGIT = true;
    public static final boolean PASSWORD_REQUIRE_SPECIAL = true;
    public static final int PASSWORD_HISTORY_COUNT = 5;
    public static final int PASSWORD_EXPIRY_DAYS = 90;
    
    // MFA
    public static final int MFA_CODE_LENGTH = 6;
    public static final int MFA_CODE_VALIDITY_SECONDS = 300; // 5 minutes
    public static final int MFA_MAX_ATTEMPTS = 3;
    public static final String MFA_ISSUER = "CC AutoPay";
    
    // Session
    public static final int SESSION_TIMEOUT_MINUTES = 30;
    public static final int REMEMBER_ME_DURATION_DAYS = 30;
    
    // Rate Limiting
    public static final int RATE_LIMIT_PER_MINUTE = 60;
    public static final int RATE_LIMIT_PER_HOUR = 1000;
    public static final int LOGIN_RATE_LIMIT_PER_MINUTE = 5;
    public static final int API_KEY_RATE_LIMIT_PER_MINUTE = 100;
    
    // CORS
    public static final String CORS_ALLOWED_ORIGINS = "${cors.allowed-origins:*}";
    public static final String CORS_ALLOWED_METHODS = "GET,POST,PUT,DELETE,OPTIONS";
    public static final String CORS_ALLOWED_HEADERS = "Authorization,Content-Type,X-Requested-With,X-Trace-Id";
    public static final String CORS_EXPOSED_HEADERS = "X-Trace-Id,X-Rate-Limit-Remaining";
    public static final long CORS_MAX_AGE = 3600;
    
    // Encryption
    public static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    public static final int ENCRYPTION_KEY_SIZE = 256;
    public static final int GCM_TAG_LENGTH = 128;
    public static final int GCM_IV_LENGTH = 12;
    
    private SecurityConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}