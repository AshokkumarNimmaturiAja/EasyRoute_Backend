package com.logistics.platform.repository;

import com.logistics.platform.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {
    List<SupportTicket> findByRaisedBy_IdOrderByCreatedAtDesc(UUID userId);
    List<SupportTicket> findAllByOrderByCreatedAtDesc();
}
