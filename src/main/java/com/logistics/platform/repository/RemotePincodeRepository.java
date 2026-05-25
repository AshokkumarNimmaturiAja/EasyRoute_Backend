package com.logistics.platform.repository;

import com.logistics.platform.entity.RemotePincode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RemotePincodeRepository extends JpaRepository<RemotePincode, UUID> {
    boolean existsByPincodeAndActiveTrue(String pincode);
}
