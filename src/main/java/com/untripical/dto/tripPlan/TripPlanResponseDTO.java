package com.untripical.dto.tripPlan;

import lombok.Value;

import java.util.Date;
import java.util.List;

@Value
public class TripPlanResponseDTO {
    Long id;
    String name;
    Date assignedAt;
    Boolean isActive;
    List<TripPlanResponseDTO> tripStops;
    Long userId;
    Long tripPlanId;
}
