package com.logistics.platform.dto.request;

import lombok.Data;
import java.util.List;
import com.logistics.platform.dto.ShipmentItemDTO;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateRequest {
    private String pickupCity;
    private String dropCity;
    private List<ShipmentItemDTO> items;
}
