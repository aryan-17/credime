package com.ccpay.auth.repository;

import com.ccpay.auth.entity.User;
import com.ccpay.auth.entity.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.emailVerified = true")
    Optional<User> findByEmailAndVerified(@Param("email") String email);
    
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :lastLoginAt WHERE u.userId = :userId")
    void updateLastLoginAt(@Param("userId") UUID userId, @Param("lastLoginAt") LocalDateTime lastLoginAt);
    
    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = :attempts WHERE u.userId = :userId")
    void updateFailedLoginAttempts(@Param("userId") UUID userId, @Param("attempts") Integer attempts);
    
    @Modifying
    @Query("UPDATE User u SET u.lockedUntil = :lockedUntil WHERE u.userId = :userId")
    void lockUserAccount(@Param("userId") UUID userId, @Param("lockedUntil") LocalDateTime lockedUntil);
    
    @Modifying
    @Query("UPDATE User u SET u.emailVerified = true, u.emailVerifiedAt = :verifiedAt WHERE u.userId = :userId")
    void verifyEmail(@Param("userId") UUID userId, @Param("verifiedAt") LocalDateTime verifiedAt);
    
    @Modifying
    @Query("UPDATE User u SET u.mfaEnabled = :enabled, u.mfaSecret = :secret WHERE u.userId = :userId")
    void updateMfaSettings(@Param("userId") UUID userId, @Param("enabled") boolean enabled, @Param("secret") String secret);
    
    @Modifying
    @Query("UPDATE User u SET u.passwordHash = :passwordHash WHERE u.userId = :userId")
    void updatePassword(@Param("userId") UUID userId, @Param("passwordHash") String passwordHash);
}