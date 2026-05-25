package com.logistics.platform.repository;

import com.logistics.platform.entity.ShipmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, UUID> {
}
