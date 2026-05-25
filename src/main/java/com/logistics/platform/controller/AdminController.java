package com.logistics.platform.controller;

import com.logistics.platform.dto.ApiResponse;
import com.logistics.platform.dto.request.AssignmentRequest;
import com.logistics.platform.dto.request.BulkAssignmentRequest;
import com.logistics.platform.dto.request.RouteMappingRequest;
import com.logistics.platform.dto.request.TruckVerifyRequest;
import com.logistics.platform.dto.request.UserSuspendRequest;
import com.logistics.platform.dto.response.AdminDashboardSummary;
import com.logistics.platform.dto.response.AuditLogResponse;
import com.logistics.platform.dto.response.RouteMappingResponse;
import com.logistics.platform.dto.response.ShipmentResponse;
import com.logistics.platform.dto.response.TruckResponse;
import com.logistics.platform.dto.response.UserProfileResponse;
import com.logistics.platform.entity.Role;
import com.logistics.platform.entity.User;
import com.logistics.platform.service.AdminService;
import com.logistics.platform.service.RouteMappingService;
import com.logistics.platform.service.ShipmentService;
import com.logistics.platform.service.TruckService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private TruckService truckService;

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private RouteMappingService routeMappingService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardSummary>> getDashboardSummary() {
        AdminDashboardSummary summary = adminService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics compiled successfully", summary));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllUsers(
            @RequestParam(name = "role", required = false) Role role,
            @RequestParam(name = "search", required = false) String search) {
        
        List<UserProfileResponse> users = adminService.getAllUsers(role, search);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    @PutMapping("/users/{id}/suspend")
    public ResponseEntity<ApiResponse<UserProfileResponse>> suspendUser(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UserSuspendRequest request,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpServletRequest) {
        
        String ipAddress = httpServletRequest.getRemoteAddr();
        UserProfileResponse response = adminService.suspendUser(id, request.getSuspend(), request.getReason(), admin, ipAddress);
        return ResponseEntity.ok(ApiResponse.success(
                request.getSuspend() ? "User account suspended" : "User account reinstated", 
                response
        ));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogs() {
        List<AuditLogResponse> logs = adminService.getAuditLogs();
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", logs));
    }

    // Vehicle verification
    @PutMapping("/trucks/{id}/verify")
    public ResponseEntity<ApiResponse<TruckResponse>> verifyTruck(
            @PathVariable("id") UUID id,
            @Valid @RequestBody TruckVerifyRequest request,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpServletRequest) {
        
        String ipAddress = httpServletRequest.getRemoteAddr();
        TruckResponse response = truckService.verifyTruck(id, request.getVerify(), request.getNotes(), admin, ipAddress);
        return ResponseEntity.ok(ApiResponse.success(
                request.getVerify() ? "Truck verified and approved" : "Truck rejected", 
                response
        ));
    }

    @GetMapping("/trucks/pending")
    public ResponseEntity<ApiResponse<List<TruckResponse>>> getPendingTrucks() {
        List<TruckResponse> response = truckService.getPendingTrucks();
        return ResponseEntity.ok(ApiResponse.success("Pending trucks fetched successfully", response));
    }

    @GetMapping("/trucks/all")
    public ResponseEntity<ApiResponse<List<TruckResponse>>> getAllTrucks() {
        List<TruckResponse> response = truckService.getAllTrucks();
        return ResponseEntity.ok(ApiResponse.success("All trucks fetched successfully", response));
    }

    // Shipment assignments
    @PutMapping("/shipments/{id}/assign")
    public ResponseEntity<ApiResponse<ShipmentResponse>> assignTruck(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AssignmentRequest request,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpServletRequest) {
        
        String ipAddress = httpServletRequest.getRemoteAddr();
        ShipmentResponse response = shipmentService.assignTruck(id, request, admin, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("Truck assigned to shipment successfully", response));
    }

    @PutMapping("/shipments/bulk-assign")
    public ResponseEntity<ApiResponse<List<ShipmentResponse>>> bulkAssign(
            @Valid @RequestBody BulkAssignmentRequest request,
            @AuthenticationPrincipal User admin,
            HttpServletRequest httpServletRequest) {
        
        String ipAddress = httpServletRequest.getRemoteAddr();
        List<ShipmentResponse> response = shipmentService.bulkAssign(request, admin, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("Consolidated shipments bulk assigned successfully", response));
    }

    // Routes
    @PostMapping("/routes")
    public ResponseEntity<ApiResponse<RouteMappingResponse>> createRouteMapping(
            @Valid @RequestBody RouteMappingRequest request) {
        
        RouteMappingResponse response = routeMappingService.createOrUpdateRouteMapping(request);
        return ResponseEntity.ok(ApiResponse.success("Route mapping saved successfully", response));
    }

    @GetMapping("/routes")
    public ResponseEntity<ApiResponse<List<RouteMappingResponse>>> getRouteMappings() {
        List<RouteMappingResponse> response = routeMappingService.getAllRouteMappings();
        return ResponseEntity.ok(ApiResponse.success("Route mappings retrieved successfully", response));
    }
}

