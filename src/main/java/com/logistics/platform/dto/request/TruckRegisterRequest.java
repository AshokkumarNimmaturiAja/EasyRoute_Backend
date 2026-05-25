package com.logistics.platform.dto.request;

import com.logistics.platform.entity.TruckType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TruckRegisterRequest {

    @NotBlank(message = "Registration number is required")
    @Size(max = 20, message = "Registration number must not exceed 20 characters")
    private String registrationNumber;

    @NotNull(message = "Truck type is required")
    private TruckType truckType;

    @NotNull(message = "Capacity is required")
    @DecimalMin(value = "0.1", message = "Capacity must be at least 0.1 tons")
    private BigDecimal capacityTons;

    @NotBlank(message = "RC document URL is required")
    @Size(max = 500, message = "RC document URL must not exceed 500 characters")
    private String rcDocumentUrl;

    @NotBlank(message = "License URL is required")
    @Size(max = 500, message = "License URL must not exceed 500 characters")
    private String licenseUrl;

    @Size(max = 500, message = "Insurance URL must not exceed 500 characters")
    private String insuranceUrl;

    @NotNull(message = "Acceptance of rate card is required")
    private Boolean rateCardAccepted;

    private String currentRouteArea;
}
