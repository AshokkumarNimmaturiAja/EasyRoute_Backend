package com.logistics.platform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistics.platform.dto.ApiResponse;
import com.logistics.platform.dto.request.PricingCalculateRequest;
import com.logistics.platform.dto.response.PricingCalculateResponse;
import com.logistics.platform.service.pricing.PricingEngineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
@Tag(name = "Pricing Engine", description = "APIs for dynamic freight calculation and courier recommendations")
public class PricingController {

    private final PricingEngineService pricingEngineService;

    @PostMapping("/calculate")
    @Operation(summary = "Calculate shipping costs and recommend couriers")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PricingCalculateResponse>> calculatePricing(
            @Valid @RequestBody PricingCalculateRequest request) {
        
        PricingCalculateResponse response = pricingEngineService.calculatePricing(request);
        return ResponseEntity.ok(ApiResponse.success("Pricing calculated successfully", response));
    }
}
