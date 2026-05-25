package com.logistics.platform.repository;

import com.logistics.platform.entity.RateCard;
import com.logistics.platform.entity.ZoneType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface RateCardRepository extends JpaRepository<RateCard, UUID> {
    List<RateCard> findByZoneTypeAndActiveTrue(ZoneType zoneType);
}
