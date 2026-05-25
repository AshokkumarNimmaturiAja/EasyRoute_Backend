package com.logistics.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteMappingResponse {
    private UUID id;
    private String pickupCity;
    private String dropCity;
    private UUID truckId;
    private String truckRegistrationNumber;
    private boolean isActive;
}
