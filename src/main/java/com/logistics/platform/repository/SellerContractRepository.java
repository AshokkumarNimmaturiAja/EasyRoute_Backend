package com.logistics.platform.repository;

import com.logistics.platform.entity.SellerContract;
import com.logistics.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SellerContractRepository extends JpaRepository<SellerContract, UUID> {
    Optional<SellerContract> findBySellerAndCourierId(User seller, UUID courierId);
}
