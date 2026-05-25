package com.logistics.platform.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistics.platform.dto.request.TruckRegisterRequest;
import com.logistics.platform.dto.response.TruckResponse;
import com.logistics.platform.entity.Role;
import com.logistics.platform.entity.Truck;
import com.logistics.platform.entity.User;
import com.logistics.platform.exception.BadRequestException;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.TruckRepository;
import com.logistics.platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TruckService {

    private final TruckRepository truckRepository;
    private final UserRepository userRepository;
    private final AuditLogService AuditLogService;
    
    
    

    @Transactional
    public TruckResponse registerTruck(TruckRegisterRequest request, String driverEmail) {
        User driver = userRepository.findByEmail(driverEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        if (driver.getRole() != Role.ROLE_DRIVER && driver.getRole() != Role.ROLE_PICKUP) {
            throw new BadRequestException("Only users with role DRIVER or PICKUP can register trucks");
        }

        if (truckRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new BadRequestException("Truck with registration number already exists");
        }

        Truck truck = Truck.builder()
                .owner(driver)
                .registrationNumber(request.getRegistrationNumber())
                .truckType(request.getTruckType())
                .capacityTons(request.getCapacityTons())
                .rcDocumentUrl(request.getRcDocumentUrl())
                .licenseUrl(request.getLicenseUrl())
                .insuranceUrl(request.getInsuranceUrl())
                .isVerified(false)
                .rateCardAccepted(request.getRateCardAccepted())
                .isAvailable(true)
                .currentRouteArea(request.getCurrentRouteArea())
                .build();

        Truck savedTruck = truckRepository.save(truck);
        return mapToResponse(savedTruck);
    }

    public List<TruckResponse> getMyTrucks(String driverEmail) {
        User driver = userRepository.findByEmail(driverEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        return truckRepository.findByOwner(driver)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TruckResponse> getPendingTrucks() {
        return truckRepository.findByIsVerifiedFalseAndIsRejectedFalse()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TruckResponse> getAllTrucks() {
        return truckRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TruckResponse getTruckById(UUID id) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found with id: " + id));
        return mapToResponse(truck);
    }

    @Transactional
    public TruckResponse verifyTruck(UUID id, boolean verify, String notes, User admin, String ipAddress) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found with id: " + id));

        truck.setVerified(verify);
        truck.setRejected(!verify);
        Truck savedTruck = truckRepository.save(truck);

        // Audit log action
        String action = verify ? "VERIFY_TRUCK" : "REJECT_TRUCK";
        AuditLogService.logAction(admin, action, "TRUCK", id, notes, ipAddress);

        return mapToResponse(savedTruck);
    }

    @Transactional
    public TruckResponse toggleAvailability(UUID id, String driverEmail) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found with id: " + id));

        if (!truck.getOwner().getEmail().equals(driverEmail)) {
            throw new BadRequestException("You do not own this truck");
        }

        truck.setAvailable(!truck.isAvailable());
        return mapToResponse(truckRepository.save(truck));
    }

    @Transactional
    public TruckResponse updateTruck(UUID truckId, com.logistics.platform.dto.request.TruckUpdateRequest request, String userEmail) {
        Truck truck = truckRepository.findById(truckId)
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found"));
        
        if (!truck.getOwner().getEmail().equals(userEmail)) {
            throw new BadRequestException("You do not have permission to update this truck");
        }

        if (request.getCapacityTons() != null) {
            truck.setCapacityTons(request.getCapacityTons());
        }
        if (request.getRcDocumentUrl() != null) {
            truck.setRcDocumentUrl(request.getRcDocumentUrl());
        }
        if (request.getLicenseUrl() != null) {
            truck.setLicenseUrl(request.getLicenseUrl());
        }
        if (request.getCurrentRouteArea() != null) {
            truck.setCurrentRouteArea(request.getCurrentRouteArea());
        }

        return mapToResponse(truckRepository.save(truck));
    }

    public List<TruckResponse> getAvailablePickupPartners(String city) {
        List<Truck> availableBikes = truckRepository.findByIsVerifiedTrueAndIsAvailableTrue();
        return availableBikes.stream()
                .filter(t -> t.getTruckType() == com.logistics.platform.entity.TruckType.TWO_WHEELER)
                .filter(t -> city == null || (t.getCurrentRouteArea() != null && t.getCurrentRouteArea().toLowerCase().contains(city.toLowerCase())))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TruckResponse mapToResponse(Truck truck) {
        return TruckResponse.builder()
                .id(truck.getId())
                .ownerId(truck.getOwner().getId())
                .ownerName(truck.getOwner().getName())
                .registrationNumber(truck.getRegistrationNumber())
                .truckType(truck.getTruckType())
                .capacityTons(truck.getCapacityTons())
                .rcDocumentUrl(truck.getRcDocumentUrl())
                .licenseUrl(truck.getLicenseUrl())
                .insuranceUrl(truck.getInsuranceUrl())
                .isVerified(truck.isVerified())
                .isRejected(truck.isRejected())
                .rateCardAccepted(truck.isRateCardAccepted())
                .isAvailable(truck.isAvailable())
                .currentRouteArea(truck.getCurrentRouteArea())
                .createdAt(truck.getCreatedAt())
                .updatedAt(truck.getUpdatedAt())
                .build();
    }
}

