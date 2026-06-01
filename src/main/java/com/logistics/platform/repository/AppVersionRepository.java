package com.logistics.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistics.platform.entity.AppVersion;

import java.util.UUID;

@Repository
public interface AppVersionRepository extends JpaRepository<AppVersion, UUID> {
    List<AppVersion> findByPlatform(String platform);
    List<AppVersion> findByStatus(String status);
    List<AppVersion> findByPlatformAndStatus(String platform, String status);
}
