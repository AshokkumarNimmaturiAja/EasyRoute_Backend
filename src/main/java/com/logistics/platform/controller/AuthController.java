package com.logistics.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistics.platform.dto.ApiResponse;
import com.logistics.platform.dto.request.LoginRequest;
import com.logistics.platform.dto.request.ProfileUpdateRequest;
import com.logistics.platform.dto.request.RegisterRequest;
import com.logistics.platform.dto.response.AuthResponse;
import com.logistics.platform.dto.response.UserProfileResponse;
import com.logistics.platform.entity.User;
import com.logistics.platform.service.AuditLogService;
import com.logistics.platform.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuditLogService auditLogService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        AuthResponse response = authService.login(request, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@AuthenticationPrincipal User user, HttpServletRequest httpRequest) {
        if (user != null) {
            String ipAddress = httpRequest.getRemoteAddr();
            auditLogService.logAction(user, "LOGOUT", "USER_SESSION", user.getId(), "User logged out", ipAddress);
        }
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> me(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileResponse profile = authService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", profile));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        UserProfileResponse profile = authService.updateProfile(userDetails.getUsername(), request, ipAddress);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }
}
