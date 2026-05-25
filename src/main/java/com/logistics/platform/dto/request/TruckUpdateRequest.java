package com.logistics.platform.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TruckUpdateRequest {
    @DecimalMin(value = "0.01", message = "Capacity must be at least 0.01 tons")
    private BigDecimal capacityTons;
    
    private String rcDocumentUrl;
    private String licenseUrl;
    private String currentRouteArea;
}
