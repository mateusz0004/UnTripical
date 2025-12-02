package com.untripical.dto.tripPlan;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.time.LocalDate;


@Value
public class TripPlanRequestDTO {

    @NotBlank(message = "name must not be blank")
    String name;

    @NotBlank(message = "date must not be null")
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate date;

}
