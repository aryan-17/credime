package com.ccpay.common.constants;

public class ApplicationConstants {
    
    // Application Info
    public static final String APP_NAME = "CC AutoPay System";
    public static final String APP_VERSION = "1.0.0";
    public static final String API_VERSION = "v1";
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";
    
    // Transaction Limits
    public static final double MIN_TRANSACTION_AMOUNT = 0.01;
    public static final double MAX_TRANSACTION_AMOUNT = 100000.00;
    public static final double DAILY_TRANSACTION_LIMIT = 50000.00;
    public static final double MONTHLY_TRANSACTION_LIMIT = 500000.00;
    
    // Account Limits
    public static final int MAX_CARDS_PER_USER = 10;
    public static final int MAX_BANK_ACCOUNTS_PER_USER = 5;
    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    public static final long ACCOUNT_LOCK_DURATION_MINUTES = 30;
    
    // Token Expiry
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 3600; // 1 hour
    public static final long REFRESH_TOKEN_VALIDITY_SECONDS = 2592000; // 30 days
    public static final long EMAIL_VERIFICATION_TOKEN_VALIDITY_HOURS = 24;
    public static final long PASSWORD_RESET_TOKEN_VALIDITY_HOURS = 1;
    
    // Cache TTL (seconds)
    public static final long USER_CACHE_TTL = 300; // 5 minutes
    public static final long WALLET_BALANCE_CACHE_TTL = 5; // 5 seconds
    public static final long CARD_DETAILS_CACHE_TTL = 3600; // 1 hour
    
    // Retry Configuration
    public static final int MAX_RETRY_ATTEMPTS = 3;
    public static final long RETRY_DELAY_SECONDS = 5;
    public static final long MAX_RETRY_DELAY_SECONDS = 60;
    
    // Email Templates
    public static final String EMAIL_VERIFICATION_TEMPLATE = "email-verification";
    public static final String PASSWORD_RESET_TEMPLATE = "password-reset";
    public static final String TRANSACTION_NOTIFICATION_TEMPLATE = "transaction-notification";
    public static final String LOW_BALANCE_ALERT_TEMPLATE = "low-balance-alert";
    
    // Kafka Topics
    public static final String USER_EVENTS_TOPIC = "user-events";
    public static final String TRANSACTION_EVENTS_TOPIC = "transaction-events";
    public static final String WALLET_EVENTS_TOPIC = "wallet-events";
    public static final String NOTIFICATION_EVENTS_TOPIC = "notification-events";
    
    // Headers
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String API_KEY_HEADER = "X-API-Key";
    public static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";
    
    // Status Values
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_VERIFIED = "VERIFIED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_SUSPENDED = "SUSPENDED";
    public static final String STATUS_DELETED = "DELETED";
    
    // Transaction Status
    public static final String TRANSACTION_PENDING = "PENDING";
    public static final String TRANSACTION_PROCESSING = "PROCESSING";
    public static final String TRANSACTION_COMPLETED = "COMPLETED";
    public static final String TRANSACTION_FAILED = "FAILED";
    public static final String TRANSACTION_CANCELLED = "CANCELLED";
    
    // KYC Status
    public static final String KYC_PENDING = "PENDING";
    public static final String KYC_VERIFIED = "VERIFIED";
    public static final String KYC_REJECTED = "REJECTED";
    public static final String KYC_EXPIRED = "EXPIRED";
    
    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATETIME_WITH_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    
    private ApplicationConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}