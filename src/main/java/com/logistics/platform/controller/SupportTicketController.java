package com.logistics.platform.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistics.platform.dto.ApiResponse;
import com.logistics.platform.dto.request.SupportTicketCreateRequest;
import com.logistics.platform.dto.request.SupportTicketReplyRequest;
import com.logistics.platform.dto.response.SupportTicketResponse;
import com.logistics.platform.service.SupportTicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    /**
     * Any authenticated user can raise a support ticket.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SupportTicketResponse>> createTicket(
            @RequestBody SupportTicketCreateRequest request) {
        SupportTicketResponse response = supportTicketService.createTicket(request);
        return ResponseEntity.ok(ApiResponse.success("Ticket raised successfully", response));
    }

    /**
     * User can see their own tickets.
     */
    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<SupportTicketResponse>>> getMyTickets() {
        List<SupportTicketResponse> tickets = supportTicketService.getMyTickets();
        return ResponseEntity.ok(ApiResponse.success("My tickets fetched", tickets));
    }

    /**
     * Admin can see ALL tickets from all users.
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SupportTicketResponse>>> getAllTickets() {
        List<SupportTicketResponse> tickets = supportTicketService.getAllTickets();
        return ResponseEntity.ok(ApiResponse.success("All tickets fetched", tickets));
    }

    /**
     * Admin can reply to and update status of a ticket.
     */
    @PutMapping("/admin/{ticketId}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> replyToTicket(
            @PathVariable("ticketId") UUID ticketId,
            @RequestBody SupportTicketReplyRequest request) {
        SupportTicketResponse response = supportTicketService.replyToTicket(ticketId, request);
        return ResponseEntity.ok(ApiResponse.success("Ticket updated successfully", response));
    }
}
