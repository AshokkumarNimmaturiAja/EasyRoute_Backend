package com.logistics.platform.repository;

import com.logistics.platform.entity.Truck;
import com.logistics.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TruckRepository extends JpaRepository<Truck, UUID> {
    List<Truck> findByOwner(User owner);
    boolean existsByRegistrationNumber(String registrationNumber);
    List<Truck> findByIsVerifiedFalseAndIsRejectedFalse();
    List<Truck> findByIsVerifiedTrueAndIsAvailableTrue();
    List<Truck> findByIsVerifiedTrueAndIsAvailableTrueAndCurrentRouteAreaContainingIgnoreCase(String routeArea);
}
