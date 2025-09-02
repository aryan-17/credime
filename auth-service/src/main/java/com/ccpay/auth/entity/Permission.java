package com.ccpay.auth.entity;

import com.ccpay.common.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "permissions")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "roles")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "permission_id")
    private UUID permissionId;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    private String description;
    
    private String resource;
    
    private String action;
    
    @ManyToMany(mappedBy = "permissions")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}