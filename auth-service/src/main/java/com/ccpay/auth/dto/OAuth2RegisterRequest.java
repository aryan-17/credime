package com.ccpay.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2RegisterRequest {
    
    @NotBlank(message = "Provider is required")
    @Pattern(regexp = "^(google|facebook|github)$", message = "Invalid provider. Supported: google, facebook, github")
    private String provider;
    
    @NotBlank(message = "OAuth2 token is required")
    private String token;
    
    // Optional fields for additional registration data
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    private boolean acceptTerms;
    
    // These fields might be pre-filled from OAuth2 provider
    private String firstName;
    private String lastName;
    
    @Email(message = "Invalid email format")
    private String email;
}