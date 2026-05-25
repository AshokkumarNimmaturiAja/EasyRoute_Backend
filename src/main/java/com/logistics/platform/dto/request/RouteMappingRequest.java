package com.logistics.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteMappingRequest {

    @NotBlank(message = "Pickup city is required")
    private String pickupCity;

    @NotBlank(message = "Drop city is required")
    private String dropCity;

    private UUID truckId; // Optional preferred truck

    private Boolean isActive;
}
