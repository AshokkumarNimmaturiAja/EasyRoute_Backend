package com.logistics.platform.repository;

import com.logistics.platform.entity.RouteMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RouteMappingRepository extends JpaRepository<RouteMapping, UUID> {
    List<RouteMapping> findByPickupCityIgnoreCaseAndDropCityIgnoreCaseAndIsActiveTrue(String pickupCity, String dropCity);
    Optional<RouteMapping> findByPickupCityIgnoreCaseAndDropCityIgnoreCaseAndTruck_Id(String pickupCity, String dropCity, UUID truckId);
}
