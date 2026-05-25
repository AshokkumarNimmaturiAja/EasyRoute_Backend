package com.logistics.platform.service.pricing;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.logistics.platform.entity.Zone;
import com.logistics.platform.entity.ZoneType;
import com.logistics.platform.repository.RemotePincodeRepository;
import com.logistics.platform.repository.ZoneRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultZoneDetectionStrategy implements ZoneDetectionStrategy {

    private final ZoneRepository zoneRepository;
    private final RemotePincodeRepository remotePincodeRepository;

    @Override
    public ZoneType detectZone(String sourcePincode, String destinationPincode) {
        if (remotePincodeRepository.existsByPincodeAndActiveTrue(sourcePincode) ||
            remotePincodeRepository.existsByPincodeAndActiveTrue(destinationPincode)) {
            return ZoneType.REMOTE;
        }

        // Check if there is an explicit zone mapping
        Optional<Zone> explicitZone = zoneRepository.findBySourcePincodeAndDestinationPincode(sourcePincode, destinationPincode);
        if (explicitZone.isPresent()) {
            return explicitZone.get().getZoneType();
        }

        // Fallback generic logic
        if (sourcePincode.equals(destinationPincode)) {
            return ZoneType.LOCAL;
        }

        // Simplified intrastate check (assume first 2 digits match implies same state in Indian pincodes)
        if (sourcePincode.substring(0, 2).equals(destinationPincode.substring(0, 2))) {
            return ZoneType.INTRASTATE;
        }

        return ZoneType.NATIONAL;
    }
}
