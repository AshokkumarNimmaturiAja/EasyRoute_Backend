package com.logistics.platform.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.logistics.platform.entity.AppVersion;
import com.logistics.platform.repository.AppVersionRepository;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppVersionService {

    private final AppVersionRepository appVersionRepository;

    public AppVersion createVersion(AppVersion appVersion) {
        return appVersionRepository.save(appVersion);
    }

    public List<AppVersion> getAllVersions() {
        return appVersionRepository.findAll();
    }

    public AppVersion updateVersionStatus(UUID id, String status) {
        AppVersion version = appVersionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AppVersion not found"));
        version.setStatus(status);
        return appVersionRepository.save(version);
    }

    public List<AppVersion> getActiveVersionsByPlatform(String platform) {
        return appVersionRepository.findByPlatformAndStatus(platform, "ACTIVE");
    }
}
