package com.logistics.platform.dto.request;

import lombok.Data;

@Data
public class SupportTicketCreateRequest {
    private String subject;
    private String description;
    private String category; // PAYMENT, SHIPMENT, VEHICLE, OTHER
}
