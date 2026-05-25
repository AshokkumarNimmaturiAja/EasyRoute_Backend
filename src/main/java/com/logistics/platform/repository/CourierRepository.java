package com.logistics.platform.repository;

import com.logistics.platform.entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourierRepository extends JpaRepository<Courier, UUID> {
    List<Courier> findByActiveTrueOrderByPriorityAsc();
}
