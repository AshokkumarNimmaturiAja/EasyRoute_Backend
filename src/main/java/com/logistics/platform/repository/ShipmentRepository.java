package com.logistics.platform.repository;

import com.logistics.platform.entity.Shipment;
import com.logistics.platform.entity.ShipmentStatus;
import com.logistics.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
    List<Shipment> findByCustomer(User customer);
    List<Shipment> findByCustomerAndStatus(User customer, ShipmentStatus status);
    List<Shipment> findByTruck_Owner(User driver);
    List<Shipment> findByPickupPartner(User pickupPartner);
    List<Shipment> findByStatus(ShipmentStatus status);
    List<Shipment> findByStatusIn(List<ShipmentStatus> statuses);
    List<Shipment> findByStatusAndPickupCityAndDropCity(ShipmentStatus status, String pickupCity, String dropCity);
}
