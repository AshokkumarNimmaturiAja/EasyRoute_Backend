package com.logistics.platform.service.pricing;

import com.logistics.platform.entity.ZoneType;

public interface ZoneDetectionStrategy {
    ZoneType detectZone(String sourcePincode, String destinationPincode);
}
