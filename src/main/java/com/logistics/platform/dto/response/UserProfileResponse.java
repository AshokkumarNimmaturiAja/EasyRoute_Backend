package com.logistics.platform.dto.response;

import com.logistics.platform.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private boolean isActive;
    private boolean emailVerified;
    private String profilePhotoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Extended profile fields
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String bio;
    private String gstin;
    private String emergencyContacts;
    private String bankDetails;
    private String preferredRouteArea;
}

