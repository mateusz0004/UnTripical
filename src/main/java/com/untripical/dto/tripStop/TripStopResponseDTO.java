package com.untripical.dto.tripStop;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class TripStopResponseDTO {
    Long id;
    String description;
    int orderIndex;
    Double distanceToNext;
    Long placeId;
}
