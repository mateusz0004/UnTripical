package com.untripical.dto.tripPlan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.Date;

@Value
public class TripPlanRequestDTO {

    @NotBlank(message = "name must not be blank")
    String name;

    @NotNull(message = "assignedAt must not be null")
    Date assignedAt;


}
