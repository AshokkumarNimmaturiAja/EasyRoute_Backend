package com.logistics.platform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequest {

    @NotNull(message = "Truck ID is required")
    private UUID truckId;

    private UUID pickupPartnerId; // Optional for Phase 1, used in Phase 2
}
