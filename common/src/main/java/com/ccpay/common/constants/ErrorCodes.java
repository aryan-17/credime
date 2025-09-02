package com.ccpay.common.constants;

public class ErrorCodes {
    
    // Authentication Errors (1000-1099)
    public static final String INVALID_CREDENTIALS = "1001";
    public static final String TOKEN_EXPIRED = "1002";
    public static final String INSUFFICIENT_PERMISSIONS = "1003";
    public static final String ACCOUNT_LOCKED = "1004";
    public static final String ACCOUNT_NOT_VERIFIED = "1005";
    public static final String MFA_REQUIRED = "1006";
    public static final String INVALID_MFA_CODE = "1007";
    public static final String SESSION_EXPIRED = "1008";
    
    // User Management Errors (1100-1199)
    public static final String USER_NOT_FOUND = "1101";
    public static final String USER_ALREADY_EXISTS = "1102";
    public static final String INVALID_USER_DATA = "1103";
    public static final String KYC_NOT_COMPLETED = "1104";
    public static final String KYC_REJECTED = "1105";
    public static final String PROFILE_UPDATE_FAILED = "1106";
    
    // Card Management Errors (2000-2099)
    public static final String INVALID_CARD_DETAILS = "2001";
    public static final String CARD_VERIFICATION_FAILED = "2002";
    public static final String CARD_ALREADY_LINKED = "2003";
    public static final String CARD_NOT_FOUND = "2004";
    public static final String CARD_EXPIRED = "2005";
    public static final String CARD_LIMIT_EXCEEDED = "2006";
    public static final String CARD_BLOCKED = "2007";
    
    // Wallet Errors (3000-3099)
    public static final String INSUFFICIENT_BALANCE = "3001";
    public static final String TRANSACTION_LIMIT_EXCEEDED = "3002";
    public static final String DUPLICATE_TRANSACTION = "3003";
    public static final String WALLET_NOT_FOUND = "3004";
    public static final String WALLET_FROZEN = "3005";
    public static final String INVALID_AMOUNT = "3006";
    public static final String CURRENCY_NOT_SUPPORTED = "3007";
    
    // Banking Errors (4000-4099)
    public static final String BANK_CONNECTION_FAILED = "4001";
    public static final String INVALID_BANK_ACCOUNT = "4002";
    public static final String TRANSFER_FAILED = "4003";
    public static final String BANK_ACCOUNT_NOT_VERIFIED = "4004";
    public static final String BANK_ACCOUNT_ALREADY_LINKED = "4005";
    public static final String ACH_LIMIT_EXCEEDED = "4006";
    
    // System Errors (5000-5099)
    public static final String INTERNAL_SERVER_ERROR = "5001";
    public static final String SERVICE_UNAVAILABLE = "5002";
    public static final String DATABASE_ERROR = "5003";
    public static final String EXTERNAL_SERVICE_ERROR = "5004";
    public static final String CONFIGURATION_ERROR = "5005";
    public static final String RATE_LIMIT_EXCEEDED = "5006";
    
    // Validation Errors (6000-6099)
    public static final String VALIDATION_FAILED = "6001";
    public static final String INVALID_REQUEST_FORMAT = "6002";
    public static final String MISSING_REQUIRED_FIELD = "6003";
    public static final String INVALID_FIELD_VALUE = "6004";
    public static final String FIELD_TOO_LONG = "6005";
    public static final String FIELD_TOO_SHORT = "6006";
    
    private ErrorCodes() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}