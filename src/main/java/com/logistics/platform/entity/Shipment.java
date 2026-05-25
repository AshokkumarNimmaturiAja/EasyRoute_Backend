package com.logistics.platform.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id")
    private Truck truck;

    // Optional field for pickup partner (introduced in Phase 2)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_partner_id")
    private User pickupPartner;

    @Column(name = "tracking_number", unique = true, length = 20)
    private String trackingNumber;

    @Column(name = "pickup_address", nullable = false, length = 500)
    private String pickupAddress;

    @Column(name = "pickup_city", nullable = false, length = 100)
    private String pickupCity;

    @Column(name = "drop_address", nullable = false, length = 500)
    private String dropAddress;

    @Column(name = "drop_city", nullable = false, length = 100)
    private String dropCity;

    @Column(name = "pickup_lat", precision = 10, scale = 7)
    private BigDecimal pickupLat;

    @Column(name = "pickup_lng", precision = 10, scale = 7)
    private BigDecimal pickupLng;

    @Column(name = "drop_lat", precision = 10, scale = 7)
    private BigDecimal dropLat;

    @Column(name = "drop_lng", precision = 10, scale = 7)
    private BigDecimal dropLng;

    @Column(name = "pickup_pincode", length = 10)
    private String pickupPincode;

    @Column(name = "drop_pincode", length = 10)
    private String dropPincode;

    @Column(name = "total_weight_kg", nullable = false, precision = 8, scale = 2)
    private BigDecimal totalWeightKg;

    @Column(name = "volumetric_weight", precision = 8, scale = 2)
    private BigDecimal volumetricWeight;

    @Column(name = "chargeable_weight", precision = 8, scale = 2)
    private BigDecimal chargeableWeight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private Courier courier;

    @Column(name = "payment_mode", length = 20)
    private String paymentMode;

    @Column(name = "cod_amount", precision = 10, scale = 2)
    private BigDecimal codAmount;

    @Column(name = "freight_charge", precision = 10, scale = 2)
    private BigDecimal freightCharge;

    @Column(name = "cod_charge", precision = 10, scale = 2)
    private BigDecimal codCharge;

    @Column(name = "fuel_charge", precision = 10, scale = 2)
    private BigDecimal fuelCharge;

    @Column(name = "gst_charge", precision = 10, scale = 2)
    private BigDecimal gstCharge;

    @Column(name = "remote_charge", precision = 10, scale = 2)
    private BigDecimal remoteCharge;

    @Column(name = "estimated_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedCost; // Total charge

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    @Column(name = "scheduled_pickup_time")
    private LocalDateTime scheduledPickupTime;

    @Column(name = "actual_pickup_time")
    private LocalDateTime actualPickupTime;

    @Column(name = "actual_delivery_time")
    private LocalDateTime actualDeliveryTime;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @PrePersist
    public void generateTrackingNumber() {
        if (this.trackingNumber == null) {
            this.trackingNumber = "ER-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    @Builder.Default
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShipmentItem> items = new ArrayList<>();

    public void addItem(ShipmentItem item) {
        items.add(item);
        item.setShipment(this);
    }

    public void removeItem(ShipmentItem item) {
        items.remove(item);
        item.setShipment(null);
    }
}
