package com.logistics.platform.controller;

import com.logistics.platform.dto.ApiResponse;
import com.logistics.platform.dto.request.BikeRegisterRequest;
import com.logistics.platform.dto.request.TruckRegisterRequest;
import com.logistics.platform.dto.response.TruckResponse;
import com.logistics.platform.entity.TruckType;
import com.logistics.platform.service.TruckService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/bikes")
public class BikeController {

    @Autowired
    private TruckService truckService;

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('DRIVER', 'PICKUP')")
    public ResponseEntity<ApiResponse<TruckResponse>> registerBike(
            @Valid @RequestBody BikeRegisterRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Convert BikeRegisterRequest to TruckRegisterRequest
        TruckRegisterRequest truckRequest = new TruckRegisterRequest();
        truckRequest.setRegistrationNumber(request.getRegistrationNumber());
        truckRequest.setTruckType(TruckType.TWO_WHEELER);
        
        // Convert kgs to tons
        BigDecimal capacityTons = request.getCapacityKgs().divide(new BigDecimal("1000"));
        truckRequest.setCapacityTons(capacityTons);
        
        truckRequest.setRcDocumentUrl(request.getRcDocumentUrl());
        truckRequest.setLicenseUrl(request.getLicenseUrl());
        truckRequest.setCurrentRouteArea(request.getCurrentRouteArea());
        truckRequest.setRateCardAccepted(request.getRateCardAccepted());
        
        TruckResponse response = truckService.registerTruck(truckRequest, userDetails.getUsername());
        
        return new ResponseEntity<>(
                ApiResponse.success("Bike registered successfully", response),
                HttpStatus.CREATED
        );
    }
}
