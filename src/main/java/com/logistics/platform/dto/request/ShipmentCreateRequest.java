package com.logistics.platform.dto.request;

import com.logistics.platform.dto.ShipmentItemDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCreateRequest {

    @NotBlank(message = "Pickup address is required")
    private String pickupAddress;

    @NotBlank(message = "Pickup city is required")
    private String pickupCity;

    @NotBlank(message = "Drop address is required")
    private String dropAddress;

    @NotBlank(message = "Drop city is required")
    private String dropCity;

    private BigDecimal pickupLat;
    private BigDecimal pickupLng;
    private BigDecimal dropLat;
    private BigDecimal dropLng;

    private BigDecimal distanceKm;

    private LocalDateTime scheduledPickupTime;

    @NotEmpty(message = "Shipment must contain at least one item")
    @Valid
    private List<ShipmentItemDTO> items;

    private String paymentMode; // ONLINE or COD
}
