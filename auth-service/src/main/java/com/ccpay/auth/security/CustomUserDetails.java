package com.ccpay.auth.security;

import com.ccpay.auth.entity.Permission;
import com.ccpay.auth.entity.Role;
import com.ccpay.auth.entity.User;
import com.ccpay.auth.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    
    private UUID userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private boolean emailVerified;
    private boolean mfaEnabled;
    private UserStatus status;
    private Set<GrantedAuthority> authorities;
    
    public static CustomUserDetails create(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Add role-based authorities
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            
            // Add permission-based authorities
            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        
        return CustomUserDetails.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailVerified(user.isEmailVerified())
                .mfaEnabled(user.isMfaEnabled())
                .status(user.getStatus())
                .authorities(authorities)
                .build();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.SUSPENDED;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE && emailVerified;
    }
}