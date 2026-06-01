package com.logistics.platform.dto.request;

import lombok.Data;

@Data
public class PaymentVerifyRequest {
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
}
