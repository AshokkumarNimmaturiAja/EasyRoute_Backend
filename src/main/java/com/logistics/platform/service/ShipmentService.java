package com.logistics.platform.service;

import com.logistics.platform.dto.ShipmentItemDTO;
import com.logistics.platform.dto.request.AssignmentRequest;
import com.logistics.platform.dto.request.BulkAssignmentRequest;
import com.logistics.platform.dto.request.ShipmentCreateRequest;
import com.logistics.platform.dto.request.ShipmentStatusUpdateRequest;
import com.logistics.platform.dto.response.ShipmentResponse;
import com.logistics.platform.entity.*;
import com.logistics.platform.exception.BadRequestException;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.ShipmentRepository;
import com.logistics.platform.repository.TruckRepository;
import com.logistics.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final TruckRepository truckRepository;
    private final AuditLogService AuditLogService;

    @Transactional
    public ShipmentResponse bookShipment(ShipmentCreateRequest request, String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (customer.getRole() != Role.ROLE_CUSTOMER) {
            throw new BadRequestException("Only users with role CUSTOMER can book shipments");
        }

        // Calculate total weight
        BigDecimal totalWeight = request.getItems().stream()
                .map(item -> item.getWeightKg().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWeight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Total cargo weight must be greater than 0");
        }

        // Fare calculation: Base fare = 500, Rate per kg = 2.5
        BigDecimal baseFare = BigDecimal.valueOf(500);
        BigDecimal perKgRate = BigDecimal.valueOf(2.5);
        BigDecimal estimatedCost = baseFare.add(totalWeight.multiply(perKgRate));

        Shipment shipment = Shipment.builder()
                .customer(customer)
                .pickupAddress(request.getPickupAddress())
                .pickupCity(request.getPickupCity())
                .dropAddress(request.getDropAddress())
                .dropCity(request.getDropCity())
                .pickupLat(request.getPickupLat())
                .pickupLng(request.getPickupLng())
                .dropLat(request.getDropLat())
                .dropLng(request.getDropLng())
                .totalWeightKg(totalWeight)
                .estimatedCost(estimatedCost)
                .status(ShipmentStatus.PENDING)
                .scheduledPickupTime(request.getScheduledPickupTime())
                .items(new ArrayList<>())
                .build();

        for (ShipmentItemDTO itemDTO : request.getItems()) {
            ShipmentItem item = ShipmentItem.builder()
                    .itemName(itemDTO.getItemName())
                    .quantity(itemDTO.getQuantity())
                    .weightKg(itemDTO.getWeightKg())
                    .description(itemDTO.getDescription())
                    .fragile(itemDTO.isFragile())
                    .build();
            shipment.addItem(item);
        }

        Shipment savedShipment = shipmentRepository.save(shipment);
        return mapToResponse(savedShipment);
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponse> getMyShipments(String email, ShipmentStatus status) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Shipment> shipments;
        if (user.getRole() == Role.ROLE_CUSTOMER) {
            if (status != null) {
                shipments = shipmentRepository.findByCustomerAndStatus(user, status);
            } else {
                shipments = shipmentRepository.findByCustomer(user);
            }
        } else if (user.getRole() == Role.ROLE_DRIVER) {
            shipments = shipmentRepository.findByTruck_Owner(user);
        } else if (user.getRole() == Role.ROLE_PICKUP) {
            shipments = shipmentRepository.findByPickupPartner(user);
        } else {
            shipments = shipmentRepository.findAll();
        }

        return shipments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ShipmentResponse getShipmentDetail(UUID id, String email) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Access checks: Admin, Customer who booked it, Driver assigned, or Pickup
        // Partner assigned
        boolean authorized = user.getRole() == Role.ROLE_ADMIN ||
                shipment.getCustomer().getId().equals(user.getId()) ||
                (shipment.getTruck() != null && shipment.getTruck().getOwner().getId().equals(user.getId())) ||
                (shipment.getPickupPartner() != null && shipment.getPickupPartner().getId().equals(user.getId()));

        if (!authorized) {
            throw new BadRequestException("You are not authorized to view this shipment");
        }

        return mapToResponse(shipment);
    }

    @Transactional
    public ShipmentResponse assignTruck(UUID shipmentId, AssignmentRequest request, User admin, String ipAddress) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

        if (shipment.getStatus() != ShipmentStatus.PENDING) {
            throw new BadRequestException("Truck can only be assigned to PENDING shipments");
        }

        Truck truck = truckRepository.findById(request.getTruckId())
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found"));

        if (!truck.isVerified()) {
            throw new BadRequestException("Cannot assign shipment to an unverified truck");
        }

        if (!truck.isAvailable()) {
            throw new BadRequestException("Truck is currently marked as unavailable");
        }

        shipment.setTruck(truck);
        shipment.setStatus(ShipmentStatus.ASSIGNED);

        if (request.getPickupPartnerId() != null) {
            User partner = userRepository.findById(request.getPickupPartnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pickup partner not found"));
            if (partner.getRole() != Role.ROLE_PICKUP) {
                throw new BadRequestException("Selected user is not a pickup partner");
            }
            shipment.setPickupPartner(partner);
        }

        Shipment savedShipment = shipmentRepository.save(shipment);

        // Audit Log
        String notes = "Assigned truck: " + truck.getRegistrationNumber();
        AuditLogService.logAction(admin, "ASSIGN_TRUCK", "SHIPMENT", shipmentId, notes, ipAddress);

        return mapToResponse(savedShipment);
    }

    @Transactional
    public List<ShipmentResponse> bulkAssign(BulkAssignmentRequest request, User admin, String ipAddress) {
        Truck truck = truckRepository.findById(request.getTruckId())
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found"));

        if (!truck.isVerified()) {
            throw new BadRequestException("Cannot assign to an unverified truck");
        }

        List<Shipment> shipments = shipmentRepository.findAllById(request.getShipmentIds());
        if (shipments.size() != request.getShipmentIds().size()) {
            throw new BadRequestException("Some shipment IDs were not found");
        }

        BigDecimal totalConsolidatedWeight = BigDecimal.ZERO;
        List<ShipmentResponse> responses = new ArrayList<>();

        for (Shipment shipment : shipments) {
            if (shipment.getStatus() != ShipmentStatus.PENDING) {
                throw new BadRequestException("Shipment " + shipment.getId() + " is not PENDING");
            }
            totalConsolidatedWeight = totalConsolidatedWeight.add(shipment.getTotalWeightKg());

            shipment.setTruck(truck);
            shipment.setStatus(ShipmentStatus.ASSIGNED);
            Shipment saved = shipmentRepository.save(shipment);

            AuditLogService.logAction(admin, "BULK_ASSIGN_TRUCK", "SHIPMENT", shipment.getId(),
                    "Bulk assigned to truck: " + truck.getRegistrationNumber(), ipAddress);

            responses.add(mapToResponse(saved));
        }

        // Validate truck capacity limit (ton = 1000kg)
        BigDecimal capacityKg = truck.getCapacityTons().multiply(BigDecimal.valueOf(1000));
        if (totalConsolidatedWeight.compareTo(capacityKg) > 0) {
            throw new BadRequestException("Consolidated shipment weight (" + totalConsolidatedWeight +
                    " kg) exceeds truck capacity (" + capacityKg + " kg)");
        }

        return responses;
    }

    @Transactional
    public ShipmentResponse updateStatus(UUID shipmentId, ShipmentStatusUpdateRequest request, User user,
            String ipAddress) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

        ShipmentStatus current = shipment.getStatus();
        ShipmentStatus target = request.getStatus();

        if (current == target) {
            return mapToResponse(shipment);
        }

        // Check permission based on roles and target states
        if (user.getRole() == Role.ROLE_CUSTOMER) {
            // Customer can only cancel from PENDING or ASSIGNED
            if (target == ShipmentStatus.CANCELLED) {
                if (current != ShipmentStatus.PENDING && current != ShipmentStatus.ASSIGNED) {
                    throw new BadRequestException("Customer can only cancel PENDING or ASSIGNED shipments");
                }
                shipment.setCancellationReason(request.getCancellationReason());
            } else {
                throw new BadRequestException("Customers can only cancel shipments");
            }
        } else if (user.getRole() == Role.ROLE_DRIVER) {
            // Drivers can transition to: PICKED, IN_TRANSIT, DELIVERED
            boolean isAssignedDriver = shipment.getTruck() != null
                    && shipment.getTruck().getOwner().getId().equals(user.getId());
            if (!isAssignedDriver) {
                throw new BadRequestException("You are not the assigned driver for this shipment");
            }

            if (target == ShipmentStatus.PICKED) {
                if (current != ShipmentStatus.ASSIGNED) {
                    throw new BadRequestException("Cannot update to PICKED from current status: " + current);
                }
                shipment.setActualPickupTime(LocalDateTime.now());
            } else if (target == ShipmentStatus.IN_TRANSIT) {
                if (current != ShipmentStatus.PICKED && current != ShipmentStatus.ASSIGNED) {
                    throw new BadRequestException("Cannot update to IN_TRANSIT from current status: " + current);
                }
                if (shipment.getActualPickupTime() == null) {
                    shipment.setActualPickupTime(LocalDateTime.now());
                }
            } else if (target == ShipmentStatus.DELIVERED) {
                if (current != ShipmentStatus.IN_TRANSIT) {
                    throw new BadRequestException("Cannot update to DELIVERED from current status: " + current);
                }
                shipment.setActualDeliveryTime(LocalDateTime.now());
            } else {
                throw new BadRequestException("Driver is not allowed to transition shipment to: " + target);
            }
        } else if (user.getRole() == Role.ROLE_PICKUP) {
            // Pickup partners can transition ASSIGNED -> PICKED (confirm pickup) and PICKED
            // -> IN_TRANSIT (handover)
            boolean isAssignedPartner = shipment.getPickupPartner() != null
                    && shipment.getPickupPartner().getId().equals(user.getId());
            if (!isAssignedPartner) {
                throw new BadRequestException("You are not the assigned pickup partner for this shipment");
            }

            if (target == ShipmentStatus.PICKED) {
                if (current != ShipmentStatus.ASSIGNED) {
                    throw new BadRequestException("Cannot update to PICKED from current status: " + current);
                }
                shipment.setActualPickupTime(LocalDateTime.now());
            } else if (target == ShipmentStatus.IN_TRANSIT) {
                if (current != ShipmentStatus.PICKED) {
                    throw new BadRequestException("Cannot transition handover to truck (IN_TRANSIT) before picking up");
                }
            } else {
                throw new BadRequestException("Pickup partner is not allowed to transition shipment to: " + target);
            }
        } else if (user.getRole() == Role.ROLE_ADMIN) {
            // Admin can transition anything. If cancelled, log the cancellation.
            if (target == ShipmentStatus.CANCELLED) {
                shipment.setCancellationReason(request.getCancellationReason() != null ? request.getCancellationReason()
                        : "Cancelled by admin");
                AuditLogService.logAction(user, "CANCEL_SHIPMENT", "SHIPMENT", shipmentId,
                        request.getCancellationReason(), ipAddress);
            } else if (target == ShipmentStatus.ASSIGNED) {
                if (shipment.getTruck() == null) {
                    throw new BadRequestException("Cannot transition status to ASSIGNED without assigning a truck");
                }
            } else if (target == ShipmentStatus.PICKED) {
                shipment.setActualPickupTime(LocalDateTime.now());
            } else if (target == ShipmentStatus.DELIVERED) {
                shipment.setActualDeliveryTime(LocalDateTime.now());
            }
        }

        shipment.setStatus(target);
        Shipment saved = shipmentRepository.save(shipment);
        return mapToResponse(saved);
    }

    public List<ShipmentResponse> getUnassignedShipments() {
        return shipmentRepository.findByStatus(ShipmentStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ShipmentResponse> findConsolidationCandidates(UUID shipmentId) {
        Shipment baseShipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

        if (baseShipment.getStatus() != ShipmentStatus.PENDING) {
            throw new BadRequestException("Shipment must be PENDING to check route consolidation");
        }

        return shipmentRepository.findByStatusAndPickupCityAndDropCity(
                ShipmentStatus.PENDING, baseShipment.getPickupCity(), baseShipment.getDropCity()).stream()
                .filter(s -> !s.getId().equals(shipmentId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShipmentResponse assignPickupPartner(UUID shipmentId, UUID partnerId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));

        if (shipment.getStatus() != ShipmentStatus.ASSIGNED) {
            throw new BadRequestException("Shipment must be ASSIGNED to a truck before assigning a pickup partner");
        }

        User partner = userRepository.findById(partnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Pickup partner not found"));

        if (partner.getRole() != Role.ROLE_PICKUP) {
            throw new BadRequestException("Selected user is not a pickup partner");
        }

        shipment.setPickupPartner(partner);
        return mapToResponse(shipmentRepository.save(shipment));
    }

    private ShipmentResponse mapToResponse(Shipment shipment) {
        List<ShipmentItemDTO> itemDTOs = shipment.getItems().stream()
                .map(item -> ShipmentItemDTO.builder()
                        .id(item.getId())
                        .itemName(item.getItemName())
                        .quantity(item.getQuantity())
                        .weightKg(item.getWeightKg())
                        .description(item.getDescription())
                        .fragile(item.isFragile())
                        .build())
                .collect(Collectors.toList());

        return ShipmentResponse.builder()
                .id(shipment.getId())
                .trackingNumber(shipment.getTrackingNumber())
                .customerId(shipment.getCustomer().getId())
                .customerName(shipment.getCustomer().getName())
                .customerPhone(shipment.getCustomer().getPhone())
                .truckId(shipment.getTruck() != null ? shipment.getTruck().getId() : null)
                .truckRegistrationNumber(
                        shipment.getTruck() != null ? shipment.getTruck().getRegistrationNumber() : null)
                .driverName(shipment.getTruck() != null ? shipment.getTruck().getOwner().getName() : null)
                .driverPhone(shipment.getTruck() != null ? shipment.getTruck().getOwner().getPhone() : null)
                .pickupPartnerId(shipment.getPickupPartner() != null ? shipment.getPickupPartner().getId() : null)
                .pickupPartnerName(shipment.getPickupPartner() != null ? shipment.getPickupPartner().getName() : null)
                .pickupAddress(shipment.getPickupAddress())
                .pickupCity(shipment.getPickupCity())
                .dropAddress(shipment.getDropAddress())
                .dropCity(shipment.getDropCity())
                .pickupLat(shipment.getPickupLat())
                .pickupLng(shipment.getPickupLng())
                .dropLat(shipment.getDropLat())
                .dropLng(shipment.getDropLng())
                .totalWeightKg(shipment.getTotalWeightKg())
                .estimatedCost(shipment.getEstimatedCost())
                .status(shipment.getStatus())
                .scheduledPickupTime(shipment.getScheduledPickupTime())
                .actualPickupTime(shipment.getActualPickupTime())
                .actualDeliveryTime(shipment.getActualDeliveryTime())
                .cancellationReason(shipment.getCancellationReason())
                .items(itemDTOs)
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .build();
    }
}
