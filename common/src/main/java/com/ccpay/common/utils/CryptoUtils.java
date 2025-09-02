package com.ccpay.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class CryptoUtils {
    
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final int AES_KEY_SIZE = 256;
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    private CryptoUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    public static String generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.encodeBase64String(secretKey.getEncoded());
    }
    
    public static String encrypt(String plainText, String keyString) throws Exception {
        byte[] key = Base64.decodeBase64(keyString);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        
        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        
        // Combine IV and cipher text
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
        
        return Base64.encodeBase64String(combined);
    }
    
    public static String decrypt(String encryptedText, String keyString) throws Exception {
        byte[] key = Base64.decodeBase64(keyString);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        
        byte[] combined = Base64.decodeBase64(encryptedText);
        
        // Extract IV and cipher text
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] cipherText = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        System.arraycopy(combined, iv.length, cipherText, 0, cipherText.length);
        
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        
        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText, StandardCharsets.UTF_8);
    }
    
    public static String hashSHA256(String input) {
        return DigestUtils.sha256Hex(input);
    }
    
    public static String hashSHA512(String input) {
        return DigestUtils.sha512Hex(input);
    }
    
    public static String hmacSHA256(String data, String key) {
        return DigestUtils.sha256Hex(key + data);
    }
    
    public static boolean verifyHash(String input, String hash) {
        String computedHash = hashSHA256(input);
        return computedHash.equals(hash);
    }
    
    public static String generateSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return Base64.encodeBase64String(salt);
    }
    
    public static String hashPassword(String password, String salt) {
        return DigestUtils.sha512Hex(salt + password);
    }
    
    public static boolean verifyPassword(String password, String salt, String hash) {
        String computedHash = hashPassword(password, salt);
        return computedHash.equals(hash);
    }
    
    public static String tokenize(String sensitiveData) {
        // Simple tokenization - in production, use a proper tokenization service
        String token = generateRandomString(16);
        // Store mapping in secure storage
        return token;
    }
    
    private static String generateRandomString(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.encodeBase64URLSafeString(randomBytes);
    }
}