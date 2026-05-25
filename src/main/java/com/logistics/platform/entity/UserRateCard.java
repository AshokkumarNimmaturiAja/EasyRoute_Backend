package com.logistics.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_rate_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRateCard extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true) // Null means it's a global default for the role
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "target_role", nullable = false)
    private Role targetRole; // ROLE_DRIVER, ROLE_PICKUP, ROLE_ADMIN, ROLE_CUSTOMER
    
    @Column(name = "name", nullable = false)
    private String name; // e.g., "Default Driver Pay", "Admin Platform Margin"

    @Column(name = "base_fare", precision = 10, scale = 2)
    private BigDecimal baseFare; 
    
    @Column(name = "per_km_rate", precision = 10, scale = 2)
    private BigDecimal perKmRate;
    
    @Column(name = "per_kg_rate", precision = 10, scale = 2)
    private BigDecimal perKgRate;
    
    @Column(name = "commission_percentage", precision = 5, scale = 2)
    private BigDecimal commissionPercentage; 
    
    @Column(name = "fixed_fee", precision = 10, scale = 2)
    private BigDecimal fixedFee;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
