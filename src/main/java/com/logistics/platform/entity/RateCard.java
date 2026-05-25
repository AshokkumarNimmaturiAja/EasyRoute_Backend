package com.logistics.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "rate_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateCard extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;

    @Enumerated(EnumType.STRING)
    @Column(name = "zone_type", nullable = false)
    private ZoneType zoneType;

    @Column(name = "weight_from", nullable = false, precision = 10, scale = 3)
    private BigDecimal weightFrom;

    @Column(name = "weight_to", nullable = false, precision = 10, scale = 3)
    private BigDecimal weightTo;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "additional_weight_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal additionalWeightPrice;

    @Column(name = "cod_fixed_fee", precision = 10, scale = 2)
    private BigDecimal codFixedFee;

    @Column(name = "cod_percentage", precision = 5, scale = 2)
    private BigDecimal codPercentage;

    @Column(name = "fuel_percentage", precision = 5, scale = 2)
    private BigDecimal fuelPercentage;

    @Column(name = "gst_percentage", precision = 5, scale = 2)
    private BigDecimal gstPercentage;

    @Column(name = "remote_area_charge", precision = 10, scale = 2)
    private BigDecimal remoteAreaCharge;

    @Column(name = "rto_percentage", precision = 5, scale = 2)
    private BigDecimal rtoPercentage;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
