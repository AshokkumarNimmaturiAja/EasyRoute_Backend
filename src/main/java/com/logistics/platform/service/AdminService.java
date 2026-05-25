package com.logistics.platform.service;

import com.logistics.platform.dto.response.AdminDashboardSummary;
import com.logistics.platform.dto.response.AuditLogResponse;
import com.logistics.platform.dto.response.UserProfileResponse;
import com.logistics.platform.entity.*;
import com.logistics.platform.exception.BadRequestException;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.AuditLogRepository;
import com.logistics.platform.repository.ShipmentRepository;
import com.logistics.platform.repository.TruckRepository;
import com.logistics.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final TruckRepository truckRepository;
    private final ShipmentRepository shipmentRepository;
    private final AuditLogRepository AuditLogRepository;
    private final AuditLogService AuditLogService;

    @Transactional
    public UserProfileResponse suspendUser(UUID userId, boolean suspend, String reason, User admin, String ipAddress) {
        if (userId.equals(admin.getId())) {
            throw new BadRequestException("You cannot suspend your own account");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setActive(!suspend);
        User saved = userRepository.save(user);

        // Audit Log
        String action = suspend ? "SUSPEND_USER" : "REINSTATE_USER";
        AuditLogService.logAction(admin, action, "USER", userId, reason, ipAddress);

        return mapToUserResponse(saved);
    }

    public List<UserProfileResponse> getAllUsers(Role role, String search) {
        // Simple filter logic in-memory for basic phase, can be optimized with JPA specifications
        return userRepository.findAll().stream()
                .filter(u -> role == null || u.getRole() == role)
                .filter(u -> search == null || u.getName().toLowerCase().contains(search.toLowerCase()) || 
                             u.getEmail().toLowerCase().contains(search.toLowerCase()))
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public AdminDashboardSummary getDashboardSummary() {
        long totalShipments = shipmentRepository.count();
        long pendingShipments = shipmentRepository.findByStatus(ShipmentStatus.PENDING).size();
        long activeShipments = shipmentRepository.findByStatusIn(
                List.of(ShipmentStatus.ASSIGNED, ShipmentStatus.PICKED, ShipmentStatus.IN_TRANSIT)
        ).size();
        long completedShipments = shipmentRepository.findByStatus(ShipmentStatus.DELIVERED).size();
        long cancelledShipments = shipmentRepository.findByStatus(ShipmentStatus.CANCELLED).size();

        long totalUsers = userRepository.count();
        long totalDrivers = userRepository.findAll().stream().filter(u -> u.getRole() == Role.ROLE_DRIVER).count();
        long totalCustomers = userRepository.findAll().stream().filter(u -> u.getRole() == Role.ROLE_CUSTOMER).count();

        long pendingTrucks = truckRepository.findByIsVerifiedFalseAndIsRejectedFalse().size();
        long activeTrucks = truckRepository.findByIsVerifiedTrueAndIsAvailableTrue().size();

        // Calculate simple financial placeholders for Phase 1
        BigDecimal totalRevenue = shipmentRepository.findByStatus(ShipmentStatus.DELIVERED)
                .stream()
                .map(Shipment::getEstimatedCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AdminDashboardSummary.builder()
                .totalShipments(totalShipments)
                .pendingShipments(pendingShipments)
                .activeShipments(activeShipments)
                .completedShipments(completedShipments)
                .cancelledShipments(cancelledShipments)
                .totalUsers(totalUsers)
                .totalDrivers(totalDrivers)
                .totalCustomers(totalCustomers)
                .pendingTrucks(pendingTrucks)
                .activeTrucks(activeTrucks)
                .totalRevenue(totalRevenue)
                .totalPayouts(totalRevenue.multiply(BigDecimal.valueOf(0.9))) // Driver gets 90%
                .platformProfit(totalRevenue.multiply(BigDecimal.valueOf(0.1))) // Platform gets 10% commission
                .build();
    }

    public List<AuditLogResponse> getAuditLogs() {
        return AuditLogRepository.findAll().stream()
                .map(log -> AuditLogResponse.builder()
                        .id(log.getId())
                        .userId(log.getUser().getId())
                        .userEmail(log.getUser().getEmail())
                        .action(log.getAction())
                        .entityType(log.getEntityType())
                        .entityId(log.getEntityId())
                        .notes(log.getNotes())
                        .ipAddress(log.getIpAddress())
                        .createdAt(log.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private UserProfileResponse mapToUserResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.isActive())
                .emailVerified(user.isEmailVerified())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

