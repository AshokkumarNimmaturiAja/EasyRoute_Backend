package com.logistics.platform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TruckVerifyRequest {

    @NotNull(message = "Verify status is required")
    private Boolean verify;

    private String notes;
}
