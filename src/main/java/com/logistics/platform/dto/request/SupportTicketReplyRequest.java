package com.logistics.platform.dto.request;

import lombok.Data;

@Data
public class SupportTicketReplyRequest {
    private String adminReply;
    private String status; // OPEN, IN_PROGRESS, RESOLVED, CLOSED
}
