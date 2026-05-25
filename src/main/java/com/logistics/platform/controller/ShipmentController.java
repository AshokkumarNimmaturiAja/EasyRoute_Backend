package com.logistics.platform.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistics.platform.dto.ApiResponse;
import com.logistics.platform.dto.request.ShipmentCreateRequest;
import com.logistics.platform.dto.request.ShipmentStatusUpdateRequest;
import com.logistics.platform.dto.response.ShipmentResponse;
import com.logistics.platform.entity.ShipmentStatus;
import com.logistics.platform.entity.User;
import com.logistics.platform.service.ShipmentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> bookShipment(
            @Valid @RequestBody ShipmentCreateRequest request,
            @AuthenticationPrincipal User user) {
        
        ShipmentResponse response = shipmentService.bookShipment(request, user.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Shipment booked successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> getMyShipments(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "status", required = false) ShipmentStatus status) {
        
        List<ShipmentResponse> response = shipmentService.getMyShipments(user.getEmail(), status);
        return ResponseEntity.ok(ApiResponse.success("Shipments retrieved successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentDetail(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal User user) {
        
        ShipmentResponse response = shipmentService.getShipmentDetail(id, user.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Shipment details retrieved successfully", response));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateStatus(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ShipmentStatusUpdateRequest request,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpServletRequest) {
        
        String ipAddress = httpServletRequest.getRemoteAddr();
        ShipmentResponse response = shipmentService.updateStatus(shipmentIdHelper(id), request, user, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("Shipment status updated successfully", response));
    }

    private UUID shipmentIdHelper(UUID id) {
        return id;
    }

    @GetMapping("/{id}/consolidation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> findConsolidationCandidates(@PathVariable("id") UUID id) {
        List<ShipmentResponse> response = shipmentService.findConsolidationCandidates(id);
        return ResponseEntity.ok(ApiResponse.success("Consolidation candidates retrieved successfully", response));
    }

    @PutMapping("/{id}/pickup-partner")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<ShipmentResponse>> assignPickupPartner(
            @PathVariable("id") UUID shipmentId,
            @RequestParam("partnerId") UUID partnerId) {
        ShipmentResponse response = shipmentService.assignPickupPartner(shipmentId, partnerId);
        return ResponseEntity.ok(ApiResponse.success("Pickup partner assigned successfully", response));
    }
}
