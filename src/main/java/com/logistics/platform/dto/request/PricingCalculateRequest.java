package com.logistics.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PricingCalculateRequest {

    @NotBlank(message = "Pickup pincode is required")
    private String pickupPincode;

    @NotBlank(message = "Delivery pincode is required")
    private String deliveryPincode;

    @NotNull(message = "Actual weight is required")
    @Positive(message = "Weight must be positive")
    private BigDecimal actualWeight;

    private BigDecimal length = BigDecimal.ZERO;
    private BigDecimal breadth = BigDecimal.ZERO;
    private BigDecimal height = BigDecimal.ZERO;

    private String paymentMode = "PREPAID";

    private BigDecimal codAmount = BigDecimal.ZERO;
    
    private String sellerEmail;
}
