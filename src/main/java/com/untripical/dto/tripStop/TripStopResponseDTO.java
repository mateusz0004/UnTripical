package com.untripical.dto.tripStop;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class TripStopResponseDTO {

    Long id;
    String description;
    LocalDateTime estimateHour;
    int orderIndex;
    Double distanceToNext;
    Long tripPlanId;
    Long placeId;

}
