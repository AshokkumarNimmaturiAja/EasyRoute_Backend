package com.logistics.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 15, message = "Phone must be between 10 and 15 digits")
    private String phone;

    @Size(max = 255, message = "Password must not exceed 255 characters")
    private String password; // Optional: if provided (not blank), update user password

    @Size(max = 500, message = "Profile Photo URL must not exceed 500 characters")
    private String profilePhotoUrl;

    // Extended profile fields
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 10, message = "Pincode must not exceed 10 characters")
    private String pincode;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    @Size(max = 15, message = "GSTIN must not exceed 15 characters")
    private String gstin;

    @Size(max = 500, message = "Emergency contacts must not exceed 500 characters")
    private String emergencyContacts;

    @Size(max = 1000, message = "Bank details must not exceed 1000 characters")
    private String bankDetails;

    @Size(max = 255, message = "Preferred route area must not exceed 255 characters")
    private String preferredRouteArea;
}
