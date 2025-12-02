package com.untripical.dto.tripStop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripStopResponseDTO {
    Long id;
    String description;
    int orderIndex;
    Double distanceToNext;
    Long placeId;
}
