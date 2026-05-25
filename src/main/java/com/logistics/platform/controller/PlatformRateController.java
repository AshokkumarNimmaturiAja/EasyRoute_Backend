package com.logistics.platform.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistics.platform.entity.UserRateCard;
import com.logistics.platform.service.pricing.RateCardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rates")
@RequiredArgsConstructor
public class PlatformRateController {

    private final RateCardService rateCardService;

    // Admin can see all global rates
    @GetMapping("/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserRateCard>> getGlobalRates() {
        return ResponseEntity.ok(rateCardService.getAllGlobalRates());
    }

    // Admin can update a rate card
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserRateCard> updateRateCard(@PathVariable("id") UUID id, @RequestBody UserRateCard updatedRate) {
        updatedRate.setId(id);
        return ResponseEntity.ok(rateCardService.saveRateCard(updatedRate));
    }

    // Any authenticated user can see their own effective rate
    @GetMapping("/my-rate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserRateCard> getMyRate(org.springframework.security.core.Authentication authentication) {
        String email = authentication.getName();
        com.logistics.platform.entity.User user = (com.logistics.platform.entity.User) authentication.getPrincipal();
        UserRateCard rate = rateCardService.getEffectiveRateForUser(user.getId(), user.getRole());
        return ResponseEntity.ok(rate);
    }
}
