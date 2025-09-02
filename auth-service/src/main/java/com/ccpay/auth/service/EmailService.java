package com.ccpay.auth.service;

import com.ccpay.auth.entity.User;
import com.ccpay.common.utils.SecurityUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.from:noreply@ccautopay.com}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Async("notificationExecutor")
    public void sendVerificationEmail(User user) {
        String token = SecurityUtils.generateSecureToken();
        String verificationLink = frontendUrl + "/verify-email?token=" + token + "&email=" + user.getEmail();
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", user.getFirstName());
        variables.put("verificationLink", verificationLink);
        
        sendEmail(user.getEmail(), "Verify Your Email", "email-verification", variables);
    }
    
    @Async("notificationExecutor")
    public void sendPasswordResetEmail(User user, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", user.getFirstName());
        variables.put("resetLink", resetLink);
        variables.put("expiryHours", "1");
        
        sendEmail(user.getEmail(), "Password Reset Request", "password-reset", variables);
    }
    
    @Async("notificationExecutor")
    public void sendLoginNotification(User user, String ipAddress, String deviceInfo) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", user.getFirstName());
        variables.put("ipAddress", ipAddress);
        variables.put("deviceInfo", deviceInfo);
        variables.put("loginTime", java.time.LocalDateTime.now().toString());
        
        sendEmail(user.getEmail(), "New Login to Your Account", "login-notification", variables);
    }
    
    @Async("notificationExecutor")
    public void sendMfaEnabledNotification(User user) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", user.getFirstName());
        
        sendEmail(user.getEmail(), "Two-Factor Authentication Enabled", "mfa-enabled", variables);
    }
    
    private void sendEmail(String to, String subject, String template, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);
            
            String htmlContent = templateEngine.process(template, context);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }
}