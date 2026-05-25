package com.logistics.platform.repository;

import com.logistics.platform.entity.Role;
import com.logistics.platform.entity.UserRateCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.util.UUID;

@Repository
public interface UserRateCardRepository extends JpaRepository<UserRateCard, UUID> {
    
    List<UserRateCard> findByTargetRoleAndActiveTrue(Role targetRole);
    
    Optional<UserRateCard> findByUserIdAndActiveTrue(UUID userId);
    
    List<UserRateCard> findByUserIdIsNullAndActiveTrue();
}
