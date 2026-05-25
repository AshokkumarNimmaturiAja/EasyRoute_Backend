package com.logistics.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zone extends BaseEntity {

    @Column(name = "source_pincode", nullable = false, length = 10)
    private String sourcePincode;

    @Column(name = "destination_pincode", nullable = false, length = 10)
    private String destinationPincode;

    @Enumerated(EnumType.STRING)
    @Column(name = "zone_type", nullable = false)
    private ZoneType zoneType;
}
