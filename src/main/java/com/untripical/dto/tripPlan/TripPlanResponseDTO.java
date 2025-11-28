package com.untripical.dto.tripPlan;

import com.untripical.dto.tripStop.TripStopResponseDTO;
import lombok.Value;

import java.util.Date;
import java.util.List;

@Value
public class TripPlanResponseDTO {
    Long id;
    String name;
    Date assignedAt;
    Boolean isActive;
    List<TripStopResponseDTO> tripStops;
    Long userId;
    Long tripPlanId;
}
