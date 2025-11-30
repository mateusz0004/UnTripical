package com.untripical.dto.tripPlan;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;

@Value
public class TripPlanRequestDTO {

    @NotBlank(message = "name must not be blank")
    String name;

    @NotBlank(message = "date must not be null")
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate date;

}
