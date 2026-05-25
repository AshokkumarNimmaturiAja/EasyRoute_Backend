package com.logistics.platform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSuspendRequest {

    @NotNull(message = "Suspend flag is required")
    private Boolean suspend;

    private String reason;
}
