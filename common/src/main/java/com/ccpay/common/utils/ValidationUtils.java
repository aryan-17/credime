package com.ccpay.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class ValidationUtils {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Phone number validation pattern (US format)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?1?[-.]?\\(?([0-9]{3})\\)?[-.]?([0-9]{3})[-.]?([0-9]{4})$"
    );
    
    // Credit card number validation pattern (basic)
    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile(
            "^[0-9]{13,19}$"
    );
    
    // Routing number validation pattern (US)
    private static final Pattern ROUTING_NUMBER_PATTERN = Pattern.compile(
            "^[0-9]{9}$"
    );
    
    // Strong password pattern
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
    );
    
    private ValidationUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    public static boolean isValidEmail(String email) {
        return StringUtils.isNotBlank(email) && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            return false;
        }
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");
        return cleaned.length() == 10 || cleaned.length() == 11;
    }
    
    public static boolean isValidCreditCardNumber(String cardNumber) {
        if (StringUtils.isBlank(cardNumber)) {
            return false;
        }
        String cleaned = cardNumber.replaceAll("[^0-9]", "");
        return CREDIT_CARD_PATTERN.matcher(cleaned).matches() && isValidLuhn(cleaned);
    }
    
    public static boolean isValidRoutingNumber(String routingNumber) {
        if (StringUtils.isBlank(routingNumber)) {
            return false;
        }
        return ROUTING_NUMBER_PATTERN.matcher(routingNumber).matches() && isValidRoutingChecksum(routingNumber);
    }
    
    public static boolean isStrongPassword(String password) {
        return StringUtils.isNotBlank(password) && STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }
    
    public static boolean isValidAmount(String amount) {
        if (StringUtils.isBlank(amount)) {
            return false;
        }
        try {
            double value = Double.parseDouble(amount);
            return value > 0 && value <= 999999.99;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isValidZipCode(String zipCode) {
        if (StringUtils.isBlank(zipCode)) {
            return false;
        }
        return zipCode.matches("^[0-9]{5}(-[0-9]{4})?$");
    }
    
    // Luhn algorithm for credit card validation
    private static boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (sum % 10 == 0);
    }
    
    // Routing number checksum validation
    private static boolean isValidRoutingChecksum(String routingNumber) {
        if (routingNumber.length() != 9) {
            return false;
        }
        
        int checksum = 0;
        int[] weights = {3, 7, 1, 3, 7, 1, 3, 7, 1};
        
        for (int i = 0; i < 9; i++) {
            checksum += Character.getNumericValue(routingNumber.charAt(i)) * weights[i];
        }
        
        return checksum % 10 == 0;
    }
    
    public static String sanitizeInput(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        // Remove any potential XSS characters
        return input.replaceAll("[<>\"'&]", "");
    }
    
    public static String maskCreditCard(String cardNumber) {
        if (StringUtils.isBlank(cardNumber) || cardNumber.length() < 4) {
            return cardNumber;
        }
        String cleaned = cardNumber.replaceAll("[^0-9]", "");
        int length = cleaned.length();
        return "**** **** **** " + cleaned.substring(length - 4);
    }
    
    public static String maskEmail(String email) {
        if (!isValidEmail(email)) {
            return email;
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        if (localPart.length() <= 3) {
            return "***@" + parts[1];
        }
        return localPart.substring(0, 3) + "***@" + parts[1];
    }
}