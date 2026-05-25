package com.logistics.platform.service;

import com.logistics.platform.dto.request.RouteMappingRequest;
import com.logistics.platform.dto.response.RouteMappingResponse;
import com.logistics.platform.entity.RouteMapping;
import com.logistics.platform.entity.Truck;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.RouteMappingRepository;
import com.logistics.platform.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteMappingService {

    private final RouteMappingRepository routeMappingRepository;
    private final TruckRepository truckRepository;

    @Transactional
    public RouteMappingResponse createOrUpdateRouteMapping(RouteMappingRequest request) {
        Optional<RouteMapping> existing = Optional.empty();
        Truck truck = null;

        if (request.getTruckId() != null) {
            truck = truckRepository.findById(request.getTruckId())
                    .orElseThrow(() -> new ResourceNotFoundException("Truck not found"));
            existing = routeMappingRepository.findByPickupCityIgnoreCaseAndDropCityIgnoreCaseAndTruck_Id(
                    request.getPickupCity(), request.getDropCity(), request.getTruckId()
            );
        }

        RouteMapping mapping;
        if (existing.isPresent()) {
            mapping = existing.get();
            if (request.getIsActive() != null) {
                mapping.setActive(request.getIsActive());
            }
        } else {
            mapping = RouteMapping.builder()
                    .pickupCity(request.getPickupCity())
                    .dropCity(request.getDropCity())
                    .truck(truck)
                    .isActive(request.getIsActive() == null || request.getIsActive())
                    .build();
        }

        RouteMapping saved = routeMappingRepository.save(mapping);
        return mapToResponse(saved);
    }

    public List<RouteMappingResponse> getAllRouteMappings() {
        return routeMappingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RouteMappingResponse> getRouteMappingsByCities(String pickupCity, String dropCity) {
        return routeMappingRepository.findByPickupCityIgnoreCaseAndDropCityIgnoreCaseAndIsActiveTrue(pickupCity, dropCity)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RouteMappingResponse mapToResponse(RouteMapping mapping) {
        return RouteMappingResponse.builder()
                .id(mapping.getId())
                .pickupCity(mapping.getPickupCity())
                .dropCity(mapping.getDropCity())
                .truckId(mapping.getTruck() != null ? mapping.getTruck().getId() : null)
                .truckRegistrationNumber(mapping.getTruck() != null ? mapping.getTruck().getRegistrationNumber() : null)
                .isActive(mapping.isActive())
                .build();
    }
}
