package com.logistics.platform.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateResponse {
    private BigDecimal distanceKm;
    private BigDecimal estimatedCost;
    private BigDecimal totalWeightKg;
}
