package com.logistics.platform.dto.response;

import com.logistics.platform.entity.TruckType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TruckResponse {
    private UUID id;
    private UUID ownerId;
    private String ownerName;
    private String registrationNumber;
    private TruckType truckType;
    private BigDecimal capacityTons;
    private String rcDocumentUrl;
    private String licenseUrl;
    private String insuranceUrl;
    private boolean isVerified;
    private boolean isRejected;
    private boolean rateCardAccepted;
    private boolean isAvailable;
    private String currentRouteArea;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
