package com.logistics.platform.repository;

import com.logistics.platform.entity.UiMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UiMetadataRepository extends JpaRepository<UiMetadata, Long> {
    Optional<UiMetadata> findByCategory(String category);
}
