package com.logistics.platform.dto.request;

import com.logistics.platform.entity.ShipmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentStatusUpdateRequest {

    @NotNull(message = "Shipment status is required")
    private ShipmentStatus status;

    private String cancellationReason;
}
