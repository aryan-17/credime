package com.ccpay.auth.security;

import com.ccpay.common.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.access-token-expiration:3600000}")
    private long accessTokenExpiration;
    
    @Value("${jwt.refresh-token-expiration:2592000000}")
    private long refreshTokenExpiration;
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_ACCESS);
        extraClaims.put(SecurityConstants.CLAIM_AUTHORITIES, 
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));
        
        return generateToken(extraClaims, userDetails, accessTokenExpiration);
    }
    
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_REFRESH);
        
        return generateToken(extraClaims, userDetails, refreshTokenExpiration);
    }
    
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuer(SecurityConstants.JWT_ISSUER)
                .setAudience(SecurityConstants.JWT_AUDIENCE)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public String getTokenType(String token) {
        return extractClaim(token, claims -> 
                (String) claims.get(SecurityConstants.CLAIM_TOKEN_TYPE));
    }
    
    public List<String> extractAuthorities(String token) {
        return extractClaim(token, claims -> {
            Object authorities = claims.get(SecurityConstants.CLAIM_AUTHORITIES);
            if (authorities instanceof List) {
                return (List<String>) authorities;
            }
            return new ArrayList<>();
        });
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}