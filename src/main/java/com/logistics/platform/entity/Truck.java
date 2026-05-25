package com.logistics.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "trucks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Truck extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "registration_number", nullable = false, unique = true, length = 20)
    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "truck_type", nullable = false)
    private TruckType truckType;

    @Column(name = "capacity_tons", nullable = false, precision = 5, scale = 2)
    private BigDecimal capacityTons;

    @Column(name = "rc_document_url", nullable = false, columnDefinition = "TEXT")
    private String rcDocumentUrl;

    @Column(name = "license_url", nullable = false, columnDefinition = "TEXT")
    private String licenseUrl;

    @Column(name = "insurance_url", columnDefinition = "TEXT")
    private String insuranceUrl;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Builder.Default
    @Column(name = "is_rejected", nullable = false, columnDefinition = "boolean default false")
    private boolean isRejected = false;

    @Builder.Default
    @Column(name = "rate_card_accepted", nullable = false)
    private boolean rateCardAccepted = false;

    @Builder.Default
    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true;

    @Column(name = "current_route_area", length = 200)
    private String currentRouteArea;
}
