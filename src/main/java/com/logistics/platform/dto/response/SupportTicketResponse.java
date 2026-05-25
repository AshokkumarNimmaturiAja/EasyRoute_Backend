package com.logistics.platform.dto.response;

import java.time.LocalDateTime;

import com.logistics.platform.entity.SupportTicket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupportTicketResponse {
    private String id;
    private String subject;
    private String description;
    private String category;
    private String status;
    private String adminReply;
    private String raisedByName;
    private String raisedByEmail;
    private String raisedByRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SupportTicketResponse from(SupportTicket ticket) {
        return SupportTicketResponse.builder()
                .id(ticket.getId().toString())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .category(ticket.getCategory())
                .status(ticket.getStatus().name())
                .adminReply(ticket.getAdminReply())
                .raisedByName(ticket.getRaisedBy().getName())
                .raisedByEmail(ticket.getRaisedBy().getEmail())
                .raisedByRole(ticket.getRaisedBy().getRole().name())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
