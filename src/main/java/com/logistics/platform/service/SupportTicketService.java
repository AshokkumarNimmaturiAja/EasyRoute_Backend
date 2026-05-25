package com.logistics.platform.service;

import com.logistics.platform.dto.request.SupportTicketCreateRequest;
import com.logistics.platform.dto.request.SupportTicketReplyRequest;
import com.logistics.platform.dto.response.SupportTicketResponse;
import com.logistics.platform.entity.SupportTicket;
import com.logistics.platform.entity.SupportTicketStatus;
import com.logistics.platform.entity.User;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.SupportTicketRepository;
import com.logistics.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public SupportTicketResponse createTicket(SupportTicketCreateRequest request) {
        User currentUser = getCurrentUser();

        SupportTicket ticket = SupportTicket.builder()
                .raisedBy(currentUser)
                .subject(request.getSubject())
                .description(request.getDescription())
                .category(request.getCategory() != null ? request.getCategory() : "OTHER")
                .status(SupportTicketStatus.OPEN)
                .build();

        SupportTicket saved = supportTicketRepository.save(ticket);
        return SupportTicketResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<SupportTicketResponse> getMyTickets() {
        User currentUser = getCurrentUser();
        return supportTicketRepository.findByRaisedBy_IdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(SupportTicketResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupportTicketResponse> getAllTickets() {
        return supportTicketRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(SupportTicketResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupportTicketResponse replyToTicket(UUID ticketId, SupportTicketReplyRequest request) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        if (request.getAdminReply() != null) {
            ticket.setAdminReply(request.getAdminReply());
        }
        if (request.getStatus() != null) {
            ticket.setStatus(SupportTicketStatus.valueOf(request.getStatus()));
        }

        SupportTicket updated = supportTicketRepository.save(ticket);
        return SupportTicketResponse.from(updated);
    }
}
