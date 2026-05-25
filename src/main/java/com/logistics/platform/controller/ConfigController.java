package com.logistics.platform.controller;

import com.logistics.platform.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Value("${google.maps.api-key:}")
    private String googleMapsApiKey;

    @GetMapping("/maps-key")
    public ResponseEntity<ApiResponse<Map<String, String>>> getMapsKey() {
        return ResponseEntity.ok(ApiResponse.success("Maps API Key retrieved successfully", Map.of("apiKey", googleMapsApiKey)));
    }
}
