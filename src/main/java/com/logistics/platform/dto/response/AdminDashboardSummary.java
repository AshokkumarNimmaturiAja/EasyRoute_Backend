package com.logistics.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardSummary {
    private long totalShipments;
    private long activeShipments; // Assigned, Picked, In Transit
    private long pendingShipments;
    private long completedShipments;
    private long cancelledShipments;
    private long totalUsers;
    private long totalDrivers;
    private long totalCustomers;
    private long pendingTrucks;
    private long activeTrucks;
    
    private BigDecimal totalRevenue;
    private BigDecimal totalPayouts;
    private BigDecimal platformProfit;
}
