package com.untripical.dto.tripStop;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class TripStopResponseDTO {
    String description;
    int orderIndex;
    Long placeId;
}
