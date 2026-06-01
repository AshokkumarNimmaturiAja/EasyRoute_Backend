package com.logistics.platform.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logistics.platform.entity.PaymentStatus;
import com.logistics.platform.entity.Role;
import com.logistics.platform.entity.ShipmentStatus;
import com.logistics.platform.entity.SupportTicketStatus;
import com.logistics.platform.entity.TruckType;
import com.logistics.platform.entity.ZoneType;
import com.logistics.platform.repository.UiMetadataRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v2/metadata")
@RequiredArgsConstructor
public class V2MetadataController {

    private final UiMetadataRepository uiMetadataRepository;

    @GetMapping("/ui-config/{category}")
    public ResponseEntity<String> getUiConfig(@PathVariable String category) {
        return uiMetadataRepository.findByCategory(category)
                .map(metadata -> ResponseEntity.ok(metadata.getValue()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/ui-config/{category}")
    public ResponseEntity<String> updateUiConfig(@PathVariable String category, @RequestBody String newValue) {
        com.logistics.platform.entity.UiMetadata metadata = uiMetadataRepository.findByCategory(category)
                .orElse(com.logistics.platform.entity.UiMetadata.builder().category(category).build());
        metadata.setValue(newValue);
        uiMetadataRepository.save(metadata);
        return ResponseEntity.ok("Settings updated successfully");
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRoles() {
        return ResponseEntity.ok(Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.toList()));
    }

    @GetMapping("/shipment-statuses")
    public ResponseEntity<List<String>> getShipmentStatuses() {
        return ResponseEntity.ok(Arrays.stream(ShipmentStatus.values()).map(Enum::name).collect(Collectors.toList()));
    }

    @GetMapping("/truck-types")
    public ResponseEntity<List<String>> getTruckTypes() {
        return ResponseEntity.ok(Arrays.stream(TruckType.values()).map(Enum::name).collect(Collectors.toList()));
    }

    @GetMapping("/payment-statuses")
    public ResponseEntity<List<String>> getPaymentStatuses() {
        return ResponseEntity.ok(Arrays.stream(PaymentStatus.values()).map(Enum::name).collect(Collectors.toList()));
    }

    @GetMapping("/support-ticket-statuses")
    public ResponseEntity<List<String>> getSupportTicketStatuses() {
        return ResponseEntity.ok(Arrays.stream(SupportTicketStatus.values()).map(Enum::name).collect(Collectors.toList()));
    }
    
    @GetMapping("/zone-types")
    public ResponseEntity<List<String>> getZoneTypes() {
        return ResponseEntity.ok(Arrays.stream(ZoneType.values()).map(Enum::name).collect(Collectors.toList()));
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, List<String>>> getAllMetadata() {
        return ResponseEntity.ok(Map.of(
            "roles", Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.toList()),
            "shipmentStatuses", Arrays.stream(ShipmentStatus.values()).map(Enum::name).collect(Collectors.toList()),
            "truckTypes", Arrays.stream(TruckType.values()).map(Enum::name).collect(Collectors.toList()),
            "paymentStatuses", Arrays.stream(PaymentStatus.values()).map(Enum::name).collect(Collectors.toList()),
            "supportTicketStatuses", Arrays.stream(SupportTicketStatus.values()).map(Enum::name).collect(Collectors.toList()),
            "zoneTypes", Arrays.stream(ZoneType.values()).map(Enum::name).collect(Collectors.toList())
        ));
    }
}
