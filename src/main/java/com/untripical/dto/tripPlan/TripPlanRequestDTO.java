package com.untripical.dto.tripPlan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class TripPlanRequestDTO {
    @NotBlank(message = "name must not be blank")
    String name;
}
