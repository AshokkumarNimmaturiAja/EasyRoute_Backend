package com.logistics.platform.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkAssignmentRequest {

    @NotEmpty(message = "Shipment IDs list cannot be empty")
    private List<UUID> shipmentIds;

    @NotNull(message = "Truck ID is required")
    private UUID truckId;
}
