package com.logistics.platform.service;

import com.logistics.platform.dto.request.LoginRequest;
import com.logistics.platform.dto.request.RegisterRequest;
import com.logistics.platform.dto.request.ProfileUpdateRequest;
import com.logistics.platform.dto.response.AuthResponse;
import com.logistics.platform.dto.response.UserProfileResponse;
import com.logistics.platform.entity.User;
import com.logistics.platform.exception.BadRequestException;
import com.logistics.platform.exception.ResourceNotFoundException;
import com.logistics.platform.repository.UserRepository;
import com.logistics.platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.logistics.platform.repository.TruckRepository;
import com.logistics.platform.entity.Truck;
import com.logistics.platform.entity.Role;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final TruckRepository truckRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number is already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .isActive(true)
                .emailVerified(false)
                .build();

        User savedUser = userRepository.save(user);

        if (request.getRole() == Role.ROLE_DRIVER && request.getRegistrationNumber() != null) {
            Truck truck = Truck.builder()
                    .owner(savedUser)
                    .registrationNumber(request.getRegistrationNumber())
                    .truckType(request.getTruckType())
                    .capacityTons(request.getCapacityTons())
                    .rcDocumentUrl(request.getRcDocumentUrl())
                    .licenseUrl(request.getLicenseUrl())
                    .isVerified(false)
                    .rateCardAccepted(true)
                    .isAvailable(true)
                    .build();
            truckRepository.save(truck);
        }

        // Generate token upon registration
        String jwt = tokenProvider.generateToken(savedUser.getEmail());

        return AuthResponse.builder()
                .token(jwt)
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request, String ipAddress) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = (User) authentication.getPrincipal();

        auditLogService.logAction(user, "LOGIN", "USER_SESSION", user.getId(), "User logged in", ipAddress);

        return AuthResponse.builder()
                .token(jwt)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return mapToProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(String email, ProfileUpdateRequest request, String ipAddress) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Phone number uniqueness check if phone is changed
        if (!user.getPhone().equals(request.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number is already registered by another user");
        }

        // Update core fields
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setProfilePhotoUrl(request.getProfilePhotoUrl());

        // Update extended profile fields
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setPincode(request.getPincode());
        user.setBio(request.getBio());
        user.setGstin(request.getGstin());
        user.setEmergencyContacts(request.getEmergencyContacts());
        user.setBankDetails(request.getBankDetails());
        user.setPreferredRouteArea(request.getPreferredRouteArea());

        // Password update (optional — only if provided)
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            if (request.getPassword().length() < 6) {
                throw new BadRequestException("Password must be at least 6 characters long");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);

        auditLogService.logAction(updatedUser, "UPDATE_PROFILE", "USER", updatedUser.getId(), "User updated profile details", ipAddress);

        return mapToProfileResponse(updatedUser);
    }

    private UserProfileResponse mapToProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.isActive())
                .emailVerified(user.isEmailVerified())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                // Extended fields
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .pincode(user.getPincode())
                .bio(user.getBio())
                .gstin(user.getGstin())
                .emergencyContacts(user.getEmergencyContacts())
                .bankDetails(user.getBankDetails())
                .preferredRouteArea(user.getPreferredRouteArea())
                .build();
    }
}

