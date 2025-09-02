package com.ccpay.auth.entity;

import com.ccpay.auth.entity.enums.KycStatus;
import com.ccpay.auth.entity.enums.UserStatus;
import com.ccpay.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status")
    @Builder.Default
    private KycStatus kycStatus = KycStatus.PENDING;
    
    @Column(name = "mfa_enabled")
    @Builder.Default
    private boolean mfaEnabled = false;
    
    @Column(name = "mfa_secret")
    private String mfaSecret;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    @Column(name = "failed_login_attempts")
    @Builder.Default
    private Integer failedLoginAttempts = 0;
    
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;
    
    @Column(name = "email_verified")
    @Builder.Default
    private boolean emailVerified = false;
    
    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RefreshToken> refreshTokens = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    
    public boolean isAccountLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }
    
    public boolean isAccountActive() {
        return status == UserStatus.ACTIVE && !isAccountLocked();
    }
    
    public void incrementFailedLoginAttempts() {
        if (failedLoginAttempts == null) {
            failedLoginAttempts = 0;
        }
        failedLoginAttempts++;
    }
    
    public void resetFailedLoginAttempts() {
        failedLoginAttempts = 0;
        lockedUntil = null;
    }
    
    public void lockAccount(long minutes) {
        lockedUntil = LocalDateTime.now().plusMinutes(minutes);
    }
}