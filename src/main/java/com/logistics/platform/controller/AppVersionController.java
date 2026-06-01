package com.logistics.platform.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistics.platform.entity.AppVersion;
import com.logistics.platform.service.AppVersionService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/versions")
@RequiredArgsConstructor
public class AppVersionController {

    private final AppVersionService appVersionService;

    @PostMapping
    public ResponseEntity<AppVersion> createVersion(@RequestBody AppVersion appVersion) {
        return ResponseEntity.ok(appVersionService.createVersion(appVersion));
    }

    @GetMapping
    public ResponseEntity<List<AppVersion>> getAllVersions() {
        return ResponseEntity.ok(appVersionService.getAllVersions());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AppVersion> updateStatus(@PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(appVersionService.updateVersionStatus(id, status));
    }
}
