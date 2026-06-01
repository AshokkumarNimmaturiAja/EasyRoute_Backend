package com.logistics.platform.controller;

import com.logistics.platform.dto.ApiResponse;
import com.logistics.platform.dto.request.TruckRegisterRequest;
import com.logistics.platform.dto.response.TruckResponse;
import com.logistics.platform.service.TruckService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trucks")
public class TruckController {

    @Autowired
    private TruckService truckService;

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('DRIVER', 'PICKUP')")
    public ResponseEntity<ApiResponse<TruckResponse>> registerTruck(
            @Valid @RequestBody TruckRegisterRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        TruckResponse response = truckService.registerTruck(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Truck registered successfully and awaiting verification", response));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('DRIVER', 'PICKUP')")
    public ResponseEntity<ApiResponse<List<TruckResponse>>> getMyTrucks(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<TruckResponse> response = truckService.getMyTrucks(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("User's trucks fetched successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TruckResponse>> getTruckById(@PathVariable("id") UUID id) {
        TruckResponse response = truckService.getTruckById(id);
        return ResponseEntity.ok(ApiResponse.success("Truck details fetched successfully", response));
    }

    @PutMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('DRIVER', 'PICKUP')")
    public ResponseEntity<ApiResponse<TruckResponse>> toggleAvailability(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        TruckResponse response = truckService.toggleAvailability(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Truck availability status toggled successfully", response));
    }

    @GetMapping("/pickup-partners")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<List<TruckResponse>>> getPickupPartners(@RequestParam(required = false) String city) {
        return ResponseEntity.ok(ApiResponse.success("Available pickup partners retrieved", truckService.getAvailablePickupPartners(city)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DRIVER', 'PICKUP')")
    public ResponseEntity<ApiResponse<TruckResponse>> updateTruck(
            @PathVariable UUID id,
            @Valid @RequestBody com.logistics.platform.dto.request.TruckUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TruckResponse response = truckService.updateTruck(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Truck updated successfully", response));
    }
}
