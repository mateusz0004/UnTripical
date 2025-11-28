package com.untripical.dto.tripStop;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class TripStopResponseDTO {

    String name;
    String description;
    LocalDateTime estimateHour;
    int orderIndex;
    Double distanceToNext;

   // Long placeId;

}
