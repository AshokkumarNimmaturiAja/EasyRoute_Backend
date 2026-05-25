package com.logistics.platform.dto.response;

import com.logistics.platform.dto.ShipmentItemDTO;
import com.logistics.platform.entity.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {
    private UUID id;
    private String trackingNumber;
    private UUID customerId;
    private String customerName;
    private String customerPhone;
    private UUID truckId;
    private String truckRegistrationNumber;
    private String driverName;
    private String driverPhone;
    private UUID pickupPartnerId;
    private String pickupPartnerName;
    
    private String pickupAddress;
    private String pickupCity;
    private String dropAddress;
    private String dropCity;
    
    private BigDecimal pickupLat;
    private BigDecimal pickupLng;
    private BigDecimal dropLat;
    private BigDecimal dropLng;
    
    private BigDecimal totalWeightKg;
    private BigDecimal estimatedCost;
    private ShipmentStatus status;
    
    private LocalDateTime scheduledPickupTime;
    private LocalDateTime actualPickupTime;
    private LocalDateTime actualDeliveryTime;
    private String cancellationReason;
    
    private List<ShipmentItemDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
