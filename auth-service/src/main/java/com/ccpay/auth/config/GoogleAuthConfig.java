package com.ccpay.auth.config;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GoogleAuthConfig {
    
    @Bean
    public GoogleAuthenticator googleAuthenticator() {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setCodeDigits(6)
                .setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30))
                .setWindowSize(3)
                .setSecretBits(160)
                .build();
        
        return new GoogleAuthenticator(config);
    }
}