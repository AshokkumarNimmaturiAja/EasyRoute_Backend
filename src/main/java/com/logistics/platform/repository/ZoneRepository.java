package com.logistics.platform.repository;

import com.logistics.platform.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, UUID> {
    Optional<Zone> findBySourcePincodeAndDestinationPincode(String sourcePincode, String destinationPincode);
}
