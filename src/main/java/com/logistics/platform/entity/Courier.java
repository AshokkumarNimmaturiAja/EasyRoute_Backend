package com.logistics.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "couriers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Courier extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Builder.Default
    @Column(nullable = false)
    private int priority = 1;

    @Column(name = "delivery_success_rate", precision = 5, scale = 2)
    private BigDecimal deliverySuccessRate;

    @Column(name = "avg_delivery_days")
    private Integer avgDeliveryDays;

    @Column(name = "rto_percentage", precision = 5, scale = 2)
    private BigDecimal rtoPercentage;
}
