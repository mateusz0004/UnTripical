package com.untripical.dto.tripStop;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class TripStopRequestDTO {

    @NotBlank(message = "description must not be blank")
    String description;

    @NotNull(message = "tripPlanId must not be null")
    Long tripPlanId;

    @NotNull(message = "placeId must not be null")
    Long placeId;
}
