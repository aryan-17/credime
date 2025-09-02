package com.ccpay.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.ccpay.auth", "com.ccpay.common"})
@EnableJpaRepositories
@EntityScan(basePackages = {"com.ccpay.auth.entity", "com.ccpay.common.audit"})
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
public class AuthServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}