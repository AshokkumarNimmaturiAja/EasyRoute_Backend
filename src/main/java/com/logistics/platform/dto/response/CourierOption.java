package com.logistics.platform.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CourierOption {
    private UUID courierId;
    private String courierName;
    private BigDecimal freight;
    private BigDecimal codCharge;
    private BigDecimal fuelCharge;
    private BigDecimal gstCharge;
    private BigDecimal remoteCharge;
    private BigDecimal total;
    
    // Revenue splits
    private BigDecimal driverPayout;
    private BigDecimal pickupPayout;
    private BigDecimal adminMargin;
    
    private Integer etaDays;
    private BigDecimal score; // Used internally for recommendation ranking
}
