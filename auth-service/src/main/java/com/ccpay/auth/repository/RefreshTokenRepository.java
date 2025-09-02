package com.ccpay.auth.repository;

import com.ccpay.auth.entity.RefreshToken;
import com.ccpay.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    
    Optional<RefreshToken> findByToken(String token);
    
    List<RefreshToken> findByUser(User user);
    
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false AND rt.expiresAt > :now")
    List<RefreshToken> findActiveTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :revokedAt, rt.revokedReason = :reason WHERE rt.token = :token")
    void revokeToken(@Param("token") String token, @Param("revokedAt") LocalDateTime revokedAt, @Param("reason") String reason);
    
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :revokedAt, rt.revokedReason = :reason WHERE rt.user = :user")
    void revokeAllUserTokens(@Param("user") User user, @Param("revokedAt") LocalDateTime revokedAt, @Param("reason") String reason);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false AND rt.expiresAt > :now")
    long countActiveTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);
}