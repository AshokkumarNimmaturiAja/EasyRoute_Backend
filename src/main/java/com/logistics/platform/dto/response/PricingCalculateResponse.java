package com.logistics.platform.dto.response;

import com.logistics.platform.entity.ZoneType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PricingCalculateResponse {
    private ZoneType zone;
    private BigDecimal volumetricWeight;
    private BigDecimal chargeableWeight;
    private List<CourierOption> courierOptions;
}
