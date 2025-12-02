package com.untripical.dto.tripPlan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.untripical.dto.tripStop.TripStopResponseDTO;
import lombok.Value;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Value
public class TripPlanResponseDTO {
    Long id;
    String name;
    Date assignedAt;
    Boolean isActive;
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate date;
    List<TripStopResponseDTO> tripStops;
    Long userId;
    Long tripPlanId;
    Double totalDistance;
}
