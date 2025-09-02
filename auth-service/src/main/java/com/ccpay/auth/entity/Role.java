package com.ccpay.auth.entity;

import com.ccpay.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"users", "permissions"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id")
    private UUID roleId;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    private String description;
    
    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<User> users = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();
}