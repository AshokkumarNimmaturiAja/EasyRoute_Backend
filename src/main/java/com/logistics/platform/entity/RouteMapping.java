package com.logistics.platform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "route_mappings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteMapping extends BaseEntity {

    @Column(name = "pickup_city", nullable = false, length = 100)
    private String pickupCity;

    @Column(name = "drop_city", nullable = false, length = 100)
    private String dropCity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id")
    private Truck truck;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
